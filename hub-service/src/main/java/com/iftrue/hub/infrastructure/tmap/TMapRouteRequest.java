package com.iftrue.hub.infrastructure.tmap;

import java.math.BigDecimal;

public record TMapRouteRequest(
        BigDecimal startX,
        BigDecimal startY,
        BigDecimal endX,
        BigDecimal endY,
        String reqCoordType,
        String resCoordType
) {
}
