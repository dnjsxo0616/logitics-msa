package com.iftrue.delivery.application.dto.deliveryroute;

import com.iftrue.delivery.domain.deliveryroute.DeliveryRoute;
import com.iftrue.delivery.domain.deliveryroute.HubDeliveryStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record DeliveryRouteResult(
        UUID routeId,
        UUID departureHubId,
        UUID arrivalHubId,
        UUID hubDeliveryManagerId,
        int sequence,
        HubDeliveryStatus status,
        BigDecimal expectedDistance,
        int expectedDuration
) {
    public static DeliveryRouteResult from(DeliveryRoute deliveryRoute) {
        return new DeliveryRouteResult(
                deliveryRoute.getId(),
                deliveryRoute.getDepartureHubId(),
                deliveryRoute.getArrivalHubId(),
                deliveryRoute.getHubDeliveryManagerId(),
                deliveryRoute.getSequence(),
                deliveryRoute.getStatus(),
                deliveryRoute.getExpectedDistance(),
                deliveryRoute.getExpectedDuration()
        );
    }
}
