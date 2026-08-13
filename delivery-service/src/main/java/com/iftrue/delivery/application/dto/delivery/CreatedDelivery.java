package com.iftrue.delivery.application.dto.delivery;

import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryStatus;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.domain.deliveryroute.DeliveryRoute;
import com.iftrue.delivery.infrastructure.client.dto.NotificationCreateRequest;

import java.math.BigDecimal;
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
        List<RouteInfo> deliveryRoutes
) {

    public static CreatedDelivery from(Delivery delivery) {
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
                delivery.getDeliveryRoutes().stream()
                        .map(RouteInfo::from)
                        .toList()
        );
    }

    public record RouteInfo(
            UUID routeId,
            UUID departureHubId,
            UUID arrivalHubId,
            UUID hubDeliveryManagerId,
            int sequence,
            BigDecimal expectedDistance,
            int expectedDuration
    ) {
        public static RouteInfo from(DeliveryRoute route) {
            return new RouteInfo(
                    route.getId(),
                    route.getDepartureHubId(),
                    route.getArrivalHubId(),
                    route.getHubDeliveryManagerId(),
                    route.getSequence(),
                    route.getExpectedDistance(),
                    route.getExpectedDuration()
            );
        }
    }
}
