package com.iftrue.hub.application.dto;

import com.iftrue.hub.domain.HubRoute;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record HubRoutePathResponseDto(
        boolean isSameHub,
        int totalDuration,
        BigDecimal totalDistance,
        List<Segment> segments
) {

    public record Segment(
            int sequence,
            UUID departureHubId,
            UUID arrivalHubId,
            int duration,
            BigDecimal distance
    ) {
        private static Segment from(int sequence, HubRoute route) {
            return new Segment(
                    sequence,
                    route.getDepartureHubId(),
                    route.getArrivalHubId(),
                    route.getDurationMinutes(),
                    route.getDistanceKm()
            );
        }
    }

    public static HubRoutePathResponseDto sameHub() {
        return new HubRoutePathResponseDto(
                true,
                0,
                BigDecimal.ZERO,
                List.of()
        );
    }

    public static HubRoutePathResponseDto direct(HubRoute route) {
        Segment segment = Segment.from(1, route);

        return new HubRoutePathResponseDto(
                false,
                segment.duration(),
                segment.distance(),
                List.of(segment)
        );
    }
}
