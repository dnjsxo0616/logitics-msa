package com.iftrue.notification.infrastructure.client.order.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderNotificationContext(
        UUID orderId,
        Instant orderedAt,
        String orderName,
        String orderSlackId,
        UUID productId,
        String productName,
        int quantity,
        String requestMessage,
        OrderStatus status
) {
}
