package com.iftrue.delivery.application.dto.delivery;

import com.iftrue.delivery.presentation.dto.delivery.ProductInfo;

import java.util.UUID;

public record DeliveryCreateCommand(
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
}
