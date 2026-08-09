package com.if_true.product.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record InventoryAdjustRequest(
	@NotNull
	UUID orderId,

	@NotNull
	@Min(1)
	Long quantity
) {
}
