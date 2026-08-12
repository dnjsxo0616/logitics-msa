package com.iftrue.order.infrastructure.client.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductResponse(
        @NotNull UUID companyId,
        @NotBlank String productName
) {
}
