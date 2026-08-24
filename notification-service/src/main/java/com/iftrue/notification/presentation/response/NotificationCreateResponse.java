package com.iftrue.notification.presentation.response;

import com.iftrue.notification.domain.aialert.AiAlert;

import java.time.Instant;
import java.util.UUID;

public record NotificationCreateResponse(
        UUID aiAlertId,
        UUID orderId,
        UUID deliveryId,
        String status,
        String failureStage,
        Instant createdAt
) {

    public static NotificationCreateResponse from(AiAlert aiAlert) {
        return new NotificationCreateResponse(
                aiAlert.getId(),
                aiAlert.getOrderId(),
                aiAlert.getDeliveryId(),
                aiAlert.getStatus().name(),
                aiAlert.getFailureStage() == null
                        ? null
                        : aiAlert.getFailureStage().name(),
                aiAlert.getCreatedAt()
        );
    }
}
