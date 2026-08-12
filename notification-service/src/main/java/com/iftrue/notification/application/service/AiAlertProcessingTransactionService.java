package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.AiAlertProcessingTarget;
import com.iftrue.notification.application.dto.GeminiDeadlineResult;
import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertRepository;
import com.iftrue.notification.domain.aialert.AiAlertStatus;
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

    private final AiAlertRepository aiAlertRepository;

    @Transactional
    public Optional<AiAlertProcessingTarget> claimNext() {
        return aiAlertRepository.findNextForProcessing(Instant.now())
                .map(aiAlert -> {
                    aiAlert.startProcessing();
                    return new AiAlertProcessingTarget(
                            aiAlert.getId(),
                            aiAlert.getOrderPayload(),
                            aiAlert.getDeliveryPayload()
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
    public void fail(UUID aiAlertId, String errorMessage) {
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
}
