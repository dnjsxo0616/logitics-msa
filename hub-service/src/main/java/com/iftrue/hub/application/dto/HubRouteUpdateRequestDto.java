package com.iftrue.hub.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class HubRouteUpdateRequestDto {

    @Min(value = 0, message = "소요시간(분)은 0(분)이상이어야 합니다")
    private Integer durationMinutes;

    @DecimalMin(value = "0", message = "이동거리(km)는 0(km)이상이어야 합니다.")
    private BigDecimal distanceKm;
}
