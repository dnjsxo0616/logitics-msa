package com.if_true.product.presentation.dto;

import com.if_true.product.domain.Product;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
	UUID id,
	UUID companyId,
	UUID hubId,
	String productName,
	String productDescription,
	Long productQuantity,
	Instant createdAt,
	UUID createdBy,
	Instant updatedAt,
	UUID updatedBy
) {
	public static ProductResponse from(Product product) {
		return new ProductResponse(
			product.getId(),
			product.getCompanyId(),
			product.getHubId(),
			product.getProductName(),
			product.getProductDescription(),
			product.getProductQuantity(),
			product.getCreatedAt(),
			product.getCreatedBy(),
			product.getUpdatedAt(),
			product.getUpdatedBy()
		);
	}
}
