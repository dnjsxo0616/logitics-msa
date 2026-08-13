package com.iftrue.order.infrastructure.client.delivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.UUID;

public record DeliveryCreateRequest(
        @NotNull UUID orderId,
        @NotNull Instant orderedAt,
        @NotNull Instant requestedArrivalAt,
        @NotNull UUID supplierCompanyId,
        @NotNull UUID recipientCompanyId,
        @NotBlank String requesterName,
        @NotBlank @Email String requesterEmail,
        @NotBlank String requesterSlackId,
        @NotNull @Valid ProductInfo productInfo,
        String requestMessage
) {

    public record ProductInfo(
            @NotNull UUID productId,
            @NotBlank String name,
            @Positive int quantity
    ) {
    }
}
