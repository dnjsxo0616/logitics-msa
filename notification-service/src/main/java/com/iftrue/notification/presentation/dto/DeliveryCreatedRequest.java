package com.iftrue.notification.presentation.dto;

import com.iftrue.notification.domain.aialert.AiRequestPayload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
        @NotNull Instant orderedAt,
        @NotNull Instant requestedArrivalAt,
        @NotNull UUID supplierCompanyId,
        @NotNull UUID recipientCompanyId,
        @NotBlank String requesterName,
        @NotBlank @Email String requesterEmail,
        @NotNull @Valid ProductInfo product,
        String requestMessage,
        @NotNull @Valid LocationInfo departureHub,
        @NotEmpty @Valid List<TransitHubInfo> transitHubs,
        @NotBlank String destinationAddress,
        @NotNull @Valid ManagerInfo departureHubManager,
        @NotNull Instant deliveryCreatedAt
) {

    public AiRequestPayload toRequestPayload() {
        return new AiRequestPayload(
                orderedAt,
                requestedArrivalAt,
                recipientCompanyId,
                supplierCompanyId,
                requesterName,
                requesterEmail,
                product.productId(),
                product.name(),
                product.quantity(),
                requestMessage,
                new AiRequestPayload.Location(
                        departureHub.hubId(),
                        departureHub.name(),
                        departureHub.address()
                ),
                transitHubs.stream()
                        .map(transitHub -> new AiRequestPayload.TransitHub(
                                transitHub.sequence(),
                                transitHub.hubId(),
                                transitHub.name(),
                                transitHub.address(),
                                transitHub.expectedDurationMinutes()
                        ))
                        .toList(),
                destinationAddress,
                new AiRequestPayload.Manager(
                        departureHubManager.userId(),
                        departureHubManager.name(),
                        departureHubManager.slackId()
                ),
                deliveryCreatedAt
        );
    }

    public record LocationInfo(
            @NotNull UUID hubId,
            @NotBlank String name,
            @NotBlank String address
    ) {
    }

    public record ProductInfo(
            @NotNull UUID productId,
            @NotBlank String name,
            @Positive int quantity
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
