package com.iftrue.delivery.infrastructure.client.dto;

import com.iftrue.delivery.application.dto.delivery.CreatedDelivery;
import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record NotificationCreateRequest(
        UUID deliveryId,
        UUID orderId,
        Instant orderedAt,
        Instant requestedArrivalAt,
        UUID supplierCompanyId,
        UUID recipientCompanyId,
        String requesterName,
        String requesterEmail,
        String requesterSlackId,
        ProductInfo product,
        String requestMessage,
        HubInfo departureHub,
        List<TransitHubInfo> transitHubs,
        String destinationAddress,
        ManagerInfo departureHubManager,
        Instant deliveryCreatedAt
) {

    public static NotificationCreateRequest of(
            CreatedDelivery createdDelivery,
            DeliveryCreateCommand command,
            CompanyResponse supplierCompany,
            CompanyResponse recipientCompany,
            HubResponse departureHub,
            List<HubResponse> transitHubs,
            UserResponse manager) {
        return new NotificationCreateRequest(
                createdDelivery.deliveryId(),
                createdDelivery.orderId(),

                command.orderedAt(),
                command.requestedArrivalAt(),

                supplierCompany.id(),
                recipientCompany.id(),

                command.requesterName(),
                command.requesterEmail(),
                command.requesterSlackId(),

                new ProductInfo(
                        command.productId(),
                        command.productName(),
                        command.productQuantity()
                ),

                command.requestMessage(),

                new HubInfo(
                        departureHub.id(),
                        departureHub.name(),
                        departureHub.address()
                ),

                createdDelivery.transitRouteInfos().stream()
                        .map(route -> {
                            HubResponse transitHub = transitHubs.stream()
                                    .filter(hub ->
                                            hub.id().equals(route.arrivalHubId())
                                    )
                                    .findFirst()
                                    .orElseThrow();

                            return new TransitHubInfo(
                                    route.sequence(),
                                    transitHub.id(),
                                    transitHub.name(),
                                    transitHub.address(),
                                    route.expectedDuration()
                            );
                        })
                        .toList(),

                recipientCompany.companyAddress(),

                new ManagerInfo(
                        manager.id(),
                        manager.name(),
                        manager.slackId()
                ),

                createdDelivery.createdAt()
        );
    }

    public record ProductInfo(
            UUID productId,
            String name,
            int quantity
    ) {
    }

    public record HubInfo(
            UUID hubId,
            String name,
            String address
    ) {
    }

    public record TransitHubInfo(
            int sequence,
            UUID hubId,
            String name,
            String address,
            int expectedDurationMinutes
    ) {
    }

    public record ManagerInfo(
            UUID userId,
            String name,
            String slackId
    ) {
    }
}