package com.iftrue.delivery.application.dto.delivery;

import java.util.UUID;

public record DeliveryCreateCommand(
        UUID orderId,
        UUID departureHubId,
        UUID destinationHubId,
        String deliveryAddress,
        String recipientName,
        String recipientSlackId,
        UUID productId,
        String productName,
        int productQuantity,
        String requestMessage
) {
}
