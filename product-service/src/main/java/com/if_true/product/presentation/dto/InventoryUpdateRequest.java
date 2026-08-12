package com.if_true.product.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryUpdateRequest(
	@NotNull
	@Min(0)
	Long productQuantity
) {
}
