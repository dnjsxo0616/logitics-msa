package com.iftrue.delivery.infrastructure.client.dto;

import java.math.BigDecimal;
import java.util.List;

public record HubRouteResponse(
        boolean isSameHub,
        int totalDuration,
        BigDecimal totalDistance,
        List<HubRouteSegment> segments
) {
}
