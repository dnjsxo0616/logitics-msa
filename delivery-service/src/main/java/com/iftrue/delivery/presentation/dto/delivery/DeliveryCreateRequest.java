package com.iftrue.delivery.presentation.dto.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;

import java.util.UUID;

public record DeliveryCreateRequest(
        UUID orderId,
        UUID departureHubId,
        UUID destinationHubId,
        String deliveryAddress,
        String recipientName,
        String recipientSlackId,
        ProductInfo productInfo,
        String requestMessage
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
            UUID productId,
            String name,
            int quantity
    ) {
    }
}
