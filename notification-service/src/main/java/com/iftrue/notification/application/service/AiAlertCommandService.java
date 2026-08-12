package com.iftrue.notification.application.service;

import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertRepository;
import com.iftrue.notification.domain.aialert.AiAlertStatus;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import com.iftrue.notification.presentation.dto.DeliveryCreatedRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiAlertCommandService {

    private final AiAlertRepository aiAlertRepository;

    public AiAlert create(DeliveryCreatedRequest request) {
        return aiAlertRepository.findByDeliveryId(request.deliveryId())
                .orElseGet(() -> createNewAlert(request));
    }

    @Transactional
    public AiAlert cancel(UUID deliveryId) {
        AiAlert aiAlert = aiAlertRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new BusinessException(
                        NotificationErrorCode.NOTIFICATION_NOT_FOUND
                ));

        if (aiAlert.getStatus() == AiAlertStatus.CANCELED) {
            return aiAlert;
        }

        aiAlert.cancel();
        return aiAlert;
    }

    private AiAlert createNewAlert(DeliveryCreatedRequest request) {
        AiAlert aiAlert = AiAlert.create(
                request.orderId(),
                request.deliveryId(),
                request.toRequestPayload()
        );

        return saveOrFindExisting(aiAlert);
    }

    private AiAlert saveOrFindExisting(AiAlert aiAlert) {
        try {
            return aiAlertRepository.saveAndFlush(aiAlert);
        } catch (DataIntegrityViolationException exception) {
            return aiAlertRepository.findByDeliveryId(aiAlert.getDeliveryId())
                    .orElseThrow(() -> exception);
        }
    }

}
