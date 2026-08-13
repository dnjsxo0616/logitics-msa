package com.iftrue.delivery.infrastructure.client.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationCreateResponse(
        UUID aiAlertId,
        UUID orderId,
        UUID deliveryId,
        String status,
        Instant createdAt
) {
}
