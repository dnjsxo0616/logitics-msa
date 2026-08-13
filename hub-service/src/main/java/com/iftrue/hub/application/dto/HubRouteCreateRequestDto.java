package com.iftrue.hub.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class HubRouteCreateRequestDto {

    @NotNull(message = "출발지는 필수입니다.")
    private UUID departureHubId;

    @NotNull(message = "도착지는 필수입니다.")
    private UUID arrivalHubId;

    @NotNull(message = "소요시간(분)은 필수입니다.")
    @Min(value = 0, message = "소요시간(분)은 0(분)이상이어야 합니다")
    private Integer durationMinutes;

    @NotNull(message = "이동거리(km)는 필수입니다.")
    @DecimalMin(value = "0", message = "이동거리(km)는 0(km)이상이어야 합니다.")
    private BigDecimal distanceKm;
}
