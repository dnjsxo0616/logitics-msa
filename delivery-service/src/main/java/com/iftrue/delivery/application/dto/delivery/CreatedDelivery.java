package com.iftrue.delivery.application.dto.delivery;

import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreatedDelivery(
        UUID deliveryId,
        UUID orderId,
        UUID departureHubId,
        UUID destinationHubId,
        UUID companyDeliveryManagerId,
        DeliveryStatus status,
        String deliveryAddress,
        String recipientName,
        String recipientSlackId,
        Instant createdAt,
        List<TransitRouteInfo> transitRouteInfos,
        UUID firstHubDeliveryManagerId
) {

    public static CreatedDelivery of(
            Delivery delivery,
            List<TransitRouteInfo> transitRoutesInfo,
            UUID firstHubDeliveryManagerId
    ) {
        return new CreatedDelivery(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getDepartureHubId(),
                delivery.getDestinationHubId(),
                delivery.getCompanyDeliveryManagerId(),
                delivery.getStatus(),
                delivery.getDeliveryAddress(),
                delivery.getRecipientName(),
                delivery.getRecipientSlackId(),
                delivery.getCreatedAt(),
                transitRoutesInfo,
                firstHubDeliveryManagerId
        );
    }

    public record TransitRouteInfo(
            UUID arrivalHubId,
            int sequence,
            int expectedDuration
    ) {
    }
}
