package com.iftrue.delivery.presentation.dto.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;

import java.util.UUID;

public record DeliveryCreateRequest(
        UUID orderId,
        UUID departureHubId,
        UUID destinationHubId,
        UUID productId,
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
                productId,
                deliveryAddress,
                recipientName,
                recipientSlackId,
                productInfo,
                requestMessage
        );
    }
}
