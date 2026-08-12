package com.iftrue.notification.infrastructure.client.order.dto;

import com.iftrue.notification.domain.aialert.OrderPayload;

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

    public OrderPayload toOrderPayload() {
        return new OrderPayload(
                orderedAt,
                requesterName,
                requesterEmail,
                productName,
                quantity,
                requestMessage,
                status.name()
        );
    }
}
