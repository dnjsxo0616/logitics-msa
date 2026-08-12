package com.iftrue.notification.application.dto;

import com.iftrue.notification.domain.aialert.DeliveryPayload;
import com.iftrue.notification.domain.aialert.OrderPayload;

import java.util.UUID;

public record AiAlertProcessingTarget(
        UUID aiAlertId,
        OrderPayload orderPayload,
        DeliveryPayload deliveryPayload
) {
}
