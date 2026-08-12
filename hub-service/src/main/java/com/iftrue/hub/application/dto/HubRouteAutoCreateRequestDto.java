package com.iftrue.hub.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class HubRouteAutoCreateRequestDto {

    @NotNull(message = "출발지는 필수입니다.")
    private UUID departureHubId;

    @NotNull(message = "도착지는 필수입니다.")
    private UUID arrivalHubId;
}
