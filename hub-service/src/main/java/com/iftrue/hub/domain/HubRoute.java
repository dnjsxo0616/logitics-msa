package com.iftrue.hub.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_hub_route", schema = "hub_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubRoute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "distance_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal distanceKm;

    public static HubRoute create(
            UUID departureHubId,
            UUID arrivalHubId,
            Integer durationMinutes,
            BigDecimal distanceKm
    ) {
        HubRoute hubRoute = new HubRoute();
        hubRoute.departureHubId = departureHubId;
        hubRoute.arrivalHubId = arrivalHubId;
        hubRoute.durationMinutes = durationMinutes;
        hubRoute.distanceKm = distanceKm;

        return hubRoute;
    }

    public void update(Integer durationMinutes, BigDecimal distanceKm) {
        if (durationMinutes != null) { this.durationMinutes = durationMinutes; }
        if (distanceKm != null) { this.distanceKm = distanceKm; }
    }
}
