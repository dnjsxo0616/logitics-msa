package com.iftrue.notification.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeliveryCanceledRequest(
        @NotNull UUID deliveryId
) {
}
