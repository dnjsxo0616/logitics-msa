package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.GeminiDeadlineResult;
import com.iftrue.notification.application.dto.NotificationCreateResult;
import com.iftrue.notification.application.exception.AiDeadlineResponseParseException;
import com.iftrue.notification.application.exception.AiDeadlineValidationException;
import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertStatus;
import com.iftrue.notification.domain.aialert.AiRequestPayload;
import com.iftrue.notification.domain.aialert.FailureStage;
import com.iftrue.notification.infrastructure.client.gemini.GeminiClient;
import com.iftrue.notification.infrastructure.client.gemini.GeminiClientException;
import com.iftrue.notification.infrastructure.client.slack.SlackClient;
import com.iftrue.notification.infrastructure.client.slack.SlackClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationCreateService {

    private final NotificationTransactionService transactionService;
    private final AiDeadlinePromptFactory promptFactory;
    private final GeminiClient geminiClient;
    private final AiDeadlineResponseParser responseParser;
    private final AiDeadlineValidator deadlineValidator;
    private final SlackMessageFormatter slackMessageFormatter;
    private final SlackClient slackClient;

    public NotificationCreateResult create(
            UUID orderId,
            UUID deliveryId,
            AiRequestPayload requestPayload
    ) {
        CreationResult creationResult = findOrCreate(
                orderId,
                deliveryId,
                requestPayload
        );
        AiAlert aiAlert = creationResult.aiAlert();

        if (aiAlert.getStatus() == AiAlertStatus.SENT) {
            return new NotificationCreateResult(
                    aiAlert,
                    creationResult.created()
            );
        }

        if (aiAlert.getStatus() == AiAlertStatus.RECEIVED) {
            aiAlert = processAi(aiAlert);
        }

        if (aiAlert.getStatus() == AiAlertStatus.AI_COMPLETED) {
            aiAlert = processSlack(aiAlert);
        }

        return new NotificationCreateResult(
                aiAlert,
                creationResult.created()
        );
    }

    private CreationResult findOrCreate(
            UUID orderId,
            UUID deliveryId,
            AiRequestPayload requestPayload
    ) {
        return transactionService.findByDeliveryId(deliveryId)
                .map(aiAlert -> new CreationResult(aiAlert, false))
                .orElseGet(() -> createNew(
                        orderId,
                        deliveryId,
                        requestPayload
                ));
    }

    private CreationResult createNew(
            UUID orderId,
            UUID deliveryId,
            AiRequestPayload requestPayload
    ) {
        try {
            AiAlert aiAlert = transactionService.createNew(
                    orderId,
                    deliveryId,
                    requestPayload
            );

            return new CreationResult(aiAlert, true);
        } catch (DataIntegrityViolationException exception) {
            AiAlert existing = transactionService
                    .findByDeliveryId(deliveryId)
                    .orElseThrow(() -> exception);

            return new CreationResult(existing, false);
        }
    }

    private AiAlert processAi(AiAlert aiAlert) {
        AiRequestPayload payload = aiAlert.getRequestPayload();
        String prompt = promptFactory.create(payload);
        String aiResponse;

        try {
            aiResponse = geminiClient.generate(prompt);
        } catch (GeminiClientException exception) {
            return recordFailure(
                    aiAlert.getId(),
                    FailureStage.AI_CALL,
                    exception
            );
        }

        GeminiDeadlineResult deadlineResult;

        try {
            deadlineResult = responseParser.parse(aiResponse);
        } catch (AiDeadlineResponseParseException exception) {
            return recordFailure(
                    aiAlert.getId(),
                    FailureStage.AI_RESPONSE_PARSE,
                    exception
            );
        }

        try {
            deadlineValidator.validate(deadlineResult, payload);
        } catch (AiDeadlineValidationException exception) {
            return recordFailure(
                    aiAlert.getId(),
                    FailureStage.AI_VALIDATION,
                    exception
            );
        }

        return transactionService.completeAi(
                aiAlert.getId(),
                prompt,
                aiResponse,
                deadlineResult.finalDeadline().toInstant()
        );
    }

    private AiAlert processSlack(AiAlert aiAlert) {
        AiRequestPayload payload = aiAlert.getRequestPayload();
        String receiverId = payload.departureHubManager().slackId();
        String message = slackMessageFormatter.format(
                aiAlert.getOrderId(),
                payload,
                aiAlert.getFinalDeadline()
        );

        try {
            slackClient.send(receiverId, message);
        } catch (SlackClientException exception) {
            return recordFailure(
                    aiAlert.getId(),
                    FailureStage.SLACK_SEND,
                    exception
            );
        }

        return transactionService.completeSlack(
                aiAlert.getId(),
                receiverId,
                message,
                Instant.now()
        );
    }

    private AiAlert recordFailure(
            UUID aiAlertId,
            FailureStage failureStage,
            RuntimeException exception
    ) {
        return transactionService.recordFailure(
                aiAlertId,
                failureStage,
                exception.getMessage()
        );
    }

    private record CreationResult(
            AiAlert aiAlert,
            boolean created
    ) {
    }
}
