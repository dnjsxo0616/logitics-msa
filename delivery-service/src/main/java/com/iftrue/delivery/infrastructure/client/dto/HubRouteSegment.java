package com.iftrue.delivery.infrastructure.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HubRouteSegment(
        UUID departureHubId,
        UUID arrivalHubId,
        int sequence,
        int duration,
        BigDecimal distance
) {
}
