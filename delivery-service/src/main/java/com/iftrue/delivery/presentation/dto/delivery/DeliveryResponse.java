package com.iftrue.delivery.presentation.dto.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryResult;
import com.iftrue.delivery.domain.delivery.DeliveryStatus;

import java.util.UUID;

public record DeliveryResponse(
        UUID deliveryId,
        UUID orderId,
        UUID companyDeliveryManagerId,
        UUID departureHubId,
        UUID destinationHubId,
        DeliveryStatus status,
        String deliveryAddress,
        String recipientName,
        String recipientSlackId
) {
    public static DeliveryResponse from(DeliveryResult deliveryResult) {
        return new DeliveryResponse(
                deliveryResult.deliveryId(),
                deliveryResult.orderId(),
                deliveryResult.companyDeliveryManagerId(),
                deliveryResult.departureHubId(),
                deliveryResult.destinationHubId(),
                deliveryResult.status(),
                deliveryResult.deliveryAddress(),
                deliveryResult.recipientName(),
                deliveryResult.recipientSlackId());
    }
}
