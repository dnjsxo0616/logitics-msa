package com.iftrue.order.infrastructure.client.delivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record DeliveryCreateRequest(
        @NotNull UUID orderId,
        @NotNull UUID departureHubId,
        @NotNull UUID destinationHubId,
        @NotBlank String deliveryAddress,
        @NotBlank String recipientName,
        @NotBlank String recipientSlackId,
        @NotNull @Valid ProductInfo productInfo,
        @NotBlank String requestMessage
) {

    public record ProductInfo(
            @NotNull UUID productId,
            @NotBlank String name,
            @Positive int quantity
    ) {
    }
}
