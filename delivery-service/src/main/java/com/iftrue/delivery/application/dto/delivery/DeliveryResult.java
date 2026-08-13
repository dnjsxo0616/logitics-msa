package com.iftrue.delivery.application.dto.delivery;

import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryStatus;

import java.util.UUID;

public record DeliveryResult(
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
    public static DeliveryResult from(Delivery delivery) {
        return new DeliveryResult(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getCompanyDeliveryManagerId(),
                delivery.getDepartureHubId(),
                delivery.getDestinationHubId(),
                delivery.getStatus(),
                delivery.getDeliveryAddress(),
                delivery.getRecipientName(),
                delivery.getRecipientSlackId());
    }
}
