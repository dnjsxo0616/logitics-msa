package com.iftrue.order.infrastructure.client.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeliveryCreateRequest(
        @NotNull UUID orderId,
        @NotNull UUID supplierCompanyId,
        @NotNull UUID receiverCompanyId,
        @NotBlank String recipientName,
        @NotBlank String recipientSlackId
) {
}
