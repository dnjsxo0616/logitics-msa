package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.AiAlertProcessingTarget;
import com.iftrue.notification.application.dto.GeminiDeadlineResult;
import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertRepository;
import com.iftrue.notification.domain.aialert.AiAlertStatus;
import com.iftrue.notification.global.config.AiDeadlineProperties;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiAlertProcessingTransactionService {

    private static final String TIMEOUT_ERROR_MESSAGE =
            NotificationErrorCode.AI_PROCESSING_TIMEOUT.getCode()
                    + ": "
                    + NotificationErrorCode.AI_PROCESSING_TIMEOUT.getMessage();

    private final AiAlertRepository aiAlertRepository;
    private final AiDeadlineProperties properties;

    @Transactional
    public void recoverTimedOut() {
        Instant now = Instant.now();
        Instant timeoutThreshold = now.minus(properties.processingTimeout());

        aiAlertRepository.findNextTimedOutProcessing(timeoutThreshold)
                .ifPresent(aiAlert -> recoverTimedOut(aiAlert, now));
    }

    @Transactional
    public Optional<AiAlertProcessingTarget> claimNext() {
        return aiAlertRepository.findNextForProcessing(Instant.now())
                .map(aiAlert -> {
                    aiAlert.startProcessing();
                    return new AiAlertProcessingTarget(
                            aiAlert.getId(),
                            aiAlert.getRequestPayload()
                    );
                });
    }

    @Transactional
    public void complete(UUID aiAlertId, GeminiDeadlineResult result) {
        AiAlert aiAlert = findById(aiAlertId);

        if (aiAlert.getStatus() != AiAlertStatus.PROCESSING) {
            return;
        }

        aiAlert.complete(
                result.prompt(),
                result.aiResponse(),
                result.finalDeadline()
        );
    }

    @Transactional
    public void handleFailure(UUID aiAlertId, String errorMessage) {
        AiAlert aiAlert = findById(aiAlertId);

        if (aiAlert.getStatus() != AiAlertStatus.PROCESSING) {
            return;
        }

        aiAlert.fail(errorMessage);
    }

    private AiAlert findById(UUID aiAlertId) {
        return aiAlertRepository.findById(aiAlertId)
                .orElseThrow(() -> new BusinessException(
                        NotificationErrorCode.NOTIFICATION_NOT_FOUND
                ));
    }

    private void recoverTimedOut(AiAlert aiAlert, Instant now) {
        aiAlert.fail(TIMEOUT_ERROR_MESSAGE);
    }
}
