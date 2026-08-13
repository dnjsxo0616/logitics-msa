package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.AiAlertProcessingTarget;
import com.iftrue.notification.application.dto.GeminiDeadlineResult;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiAlertProcessor {

    private final AiAlertProcessingTransactionService transactionService;
    private final AiDeadlineGenerationService deadlineGenerationService;
    private final SlackMessageProcessor slackMessageProcessor;

    @Scheduled(fixedDelayString = "${notification.ai.processing-interval}")
    public void processNext() {
        transactionService.recoverTimedOut();
        transactionService.claimNext().ifPresent(this::process);
    }

    private void process(AiAlertProcessingTarget target) {
        GeminiDeadlineResult result;

        try {
            result = deadlineGenerationService.generate(
                    target.requestPayload()
            );
        } catch (RuntimeException exception) {
            transactionService.handleFailure(
                    target.aiAlertId(),
                    getErrorMessage(exception)
            );
            return;
        }

        transactionService.complete(target.aiAlertId(), result);
        slackMessageProcessor.processForAiAlert(target.aiAlertId());
    }

    private String getErrorMessage(RuntimeException exception) {
        if (exception instanceof BusinessException businessException) {
            return businessException.getErrorCode().getCode()
                    + ": "
                    + businessException.getErrorCode().getMessage();
        }

        return NotificationErrorCode.AI_PROCESSING_FAILED.getCode()
                + ": "
                + NotificationErrorCode.AI_PROCESSING_FAILED.getMessage();
    }
}
