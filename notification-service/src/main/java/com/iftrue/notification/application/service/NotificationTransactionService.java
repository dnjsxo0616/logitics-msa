package com.iftrue.notification.application.service;

import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertRepository;
import com.iftrue.notification.domain.aialert.AiRequestPayload;
import com.iftrue.notification.domain.aialert.FailureStage;
import com.iftrue.notification.domain.slackmessage.SlackMessage;
import com.iftrue.notification.domain.slackmessage.SlackMessageRepository;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationTransactionService {

    private final AiAlertRepository aiAlertRepository;
    private final SlackMessageRepository slackMessageRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AiAlert createNew(
            UUID orderId,
            UUID deliveryId,
            AiRequestPayload requestPayload
    ) {
        AiAlert aiAlert = AiAlert.create(
                orderId,
                deliveryId,
                requestPayload
        );

        return aiAlertRepository.saveAndFlush(aiAlert);
    }

    @Transactional(readOnly = true)
    public Optional<AiAlert> findByDeliveryId(UUID deliveryId) {
        return aiAlertRepository.findByDeliveryId(deliveryId);
    }

    @Transactional
    public AiAlert completeAi(
            UUID aiAlertId,
            String prompt,
            String aiResponse,
            Instant finalDeadline
    ) {
        AiAlert aiAlert = getAiAlert(aiAlertId);

        aiAlert.completeAi(
                prompt,
                aiResponse,
                finalDeadline
        );

        return aiAlert;
    }

    @Transactional
    public AiAlert recordFailure(
            UUID aiAlertId,
            FailureStage stage,
            String message
    ) {
        AiAlert aiAlert = getAiAlert(aiAlertId);
        aiAlert.recordFailure(stage, message);

        return aiAlert;
    }

    @Transactional
    public AiAlert completeSlack(
            UUID aiAlertId,
            String receiverId,
            String message,
            Instant sentAt
    ) {
        AiAlert aiAlert = getAiAlert(aiAlertId);

        SlackMessage slackMessage =
                SlackMessage.createForAiAlert(
                        aiAlert,
                        receiverId,
                        message,
                        sentAt
                );

        slackMessageRepository.save(slackMessage);
        aiAlert.completeSlack();

        return aiAlert;
    }

    private AiAlert getAiAlert(UUID aiAlertId) {
        return aiAlertRepository.findById(aiAlertId)
                .orElseThrow(() -> new BusinessException(
                        NotificationErrorCode.AI_ALERT_NOT_FOUND
                ));
    }
}
