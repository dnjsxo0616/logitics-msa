package com.if_true.product.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ProductUpdateRequest(
	UUID companyId,
	UUID hubId,
	@Size(max = 100)
	String productName,
	String productDescription,
	@Min(0)
	Long productQuantity
) {
}
