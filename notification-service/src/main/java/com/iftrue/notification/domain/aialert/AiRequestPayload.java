package com.iftrue.notification.domain.aialert;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AiRequestPayload(
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
        Instant deliveryCreatedAt,
        int totalExpectedDurationMinutes
) {

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
