package com.if_true.product.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryAdjustRequest(
	@NotNull
	@Min(0)
	Long quantity
) {
}
