package com.iftrue.hub.application.metrics;

import java.math.BigDecimal;

public record HubRouteMetrics(
        int durationMinutes,
        BigDecimal distanceKm
) {
}
