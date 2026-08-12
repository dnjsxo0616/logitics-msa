package com.iftrue.notification.presentation.dto;

import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertStatus;

import java.time.Instant;
import java.util.UUID;

public record AiAlertResponse(
        UUID aiAlertId,
        UUID orderId,
        UUID deliveryId,
        AiAlertStatus status,
        Instant createdAt
) {

    public static AiAlertResponse from(AiAlert aiAlert) {
        return new AiAlertResponse(
                aiAlert.getId(),
                aiAlert.getOrderId(),
                aiAlert.getDeliveryId(),
                aiAlert.getStatus(),
                aiAlert.getCreatedAt()
        );
    }
}
