package com.iftrue.hub.application.dto;

import com.iftrue.hub.domain.HubRoute;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record HubRouteResponseDto(
        UUID id,
        UUID departureHubId,
        UUID arrivalHubId,
        Integer durationMinutes,
        BigDecimal distanceKm,

        Instant createdAt,
        Instant updatedAt
) {
    public static HubRouteResponseDto from(HubRoute hubRoute) {
        return new HubRouteResponseDto(
                hubRoute.getId(),
                hubRoute.getDepartureHubId(),
                hubRoute.getArrivalHubId(),
                hubRoute.getDurationMinutes(),
                hubRoute.getDistanceKm(),
                hubRoute.getCreatedAt(),
                hubRoute.getUpdatedAt()
        );
    }
}
