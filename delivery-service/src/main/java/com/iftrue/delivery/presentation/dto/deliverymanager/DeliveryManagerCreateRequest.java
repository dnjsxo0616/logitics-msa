package com.iftrue.delivery.presentation.dto.deliverymanager;

import com.iftrue.delivery.application.service.deliverymanager.DeliveryManagerCreateCommand;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeliveryManagerCreateRequest(
        @NotNull UUID userId,
        @NotBlank String slackId,
        UUID hubId,
        @NotNull DeliveryManagerType type
) {
    public DeliveryManagerCreateCommand toCommand() {
        return new DeliveryManagerCreateCommand(
                userId,
                slackId,
                hubId,
                type
        );
    }
}
