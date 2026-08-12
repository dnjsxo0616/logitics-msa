package com.iftrue.delivery.presentation.dto.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;
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
        @NotNull ProductInfo productInfo,
        @NotBlank String requestMessage
) {
    public DeliveryCreateCommand toCommand() {
        return new DeliveryCreateCommand(
                orderId,
                departureHubId,
                destinationHubId,
                deliveryAddress,
                recipientName,
                recipientSlackId,
                productInfo.productId(),
                productInfo.name(),
                productInfo.quantity(),
                requestMessage
        );
    }

    public record ProductInfo(
            @NotNull UUID productId,
            @NotBlank String name,
            @Positive int quantity
    ) {
    }
}
