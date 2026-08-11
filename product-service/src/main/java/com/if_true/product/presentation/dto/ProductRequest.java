package com.if_true.product.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ProductRequest(
	@NotNull
	UUID companyId,

	@NotNull
	UUID hubId,

	@NotBlank
	@Size(max = 100)
	String productName,

	String productDescription,

	@NotNull
	@Min(0)
	Long productQuantity
) {
}
