package com.iftrue.order.presentation.dto;

import com.iftrue.order.domain.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OrderNotificationContextResponse(
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
