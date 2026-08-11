package com.iftrue.notification.infrastructure.client.order.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderNotificationContext(
        UUID orderId,
        Instant orderedAt,
        String requesterName,
        String requesterEmail,
        String productName,
        int quantity,
        String requestMessage,
        OrderStatus status
) {
}
