package com.iftrue.notification.domain.aialert;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DeliveryPayload(
        Location departureHub,
        List<TransitHub> transitHubs,
        String destinationAddress,
        Manager departureHubManager,
        Instant deliveryCreatedAt
) {
    public record Location(
            UUID hubId,
            String name,
            String address
    ) {
    }

    public record TransitHub(
            int sequence,
            UUID hubId,
            String name,
            String address,
            int expectedDurationMinutes
    ) {
    }

    public record Manager(
            UUID userId,
            String name,
            String slackId
    ) {
    }
}
