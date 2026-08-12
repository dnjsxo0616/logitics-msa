package com.iftrue.notification.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DeliveryCreatedRequest(
        @NotNull UUID deliveryId,
        @NotNull UUID orderId,
        @NotNull @Valid LocationInfo departureHub,
        @NotEmpty @Valid List<TransitHubInfo> transitHubs,
        @NotBlank String destinationAddress,
        @NotNull @Valid ManagerInfo departureHubManager,
        @NotNull Instant deliveryCreatedAt
) {
    public record LocationInfo(
            @NotNull UUID hubId,
            @NotBlank String name,
            @NotBlank String address
    ) {
    }

    public record TransitHubInfo(
            @Positive int sequence,
            @NotNull UUID hubId,
            @NotBlank String name,
            @NotBlank String address,
            @PositiveOrZero int expectedDurationMinutes
    ) {
    }

    public record ManagerInfo(
            @NotNull UUID userId,
            @NotBlank String name,
            @NotBlank String slackId
    ) {
    }
}
