package com.iftrue.notification.infrastructure.client.order.dto;

import com.iftrue.notification.domain.aialert.OrderPayload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.UUID;

public record OrderNotificationContext(
        @NotNull UUID orderId,
        @NotNull Instant orderedAt,
        @NotBlank String requesterName,
        @NotBlank @Email String requesterEmail,
        @NotBlank String productName,
        @Positive int quantity,
        String requestMessage,
        @NotNull OrderStatus status
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
