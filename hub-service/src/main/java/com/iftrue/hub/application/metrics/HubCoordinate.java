package com.iftrue.hub.application.metrics;

import java.math.BigDecimal;

public record HubCoordinate(
        BigDecimal latitude,
        BigDecimal longitude
) {
}
