package com.if_true.product.presentation.dto;

import com.if_true.product.domain.Product;
import java.util.UUID;

public record InternalProductResponse(
	UUID productId,
	UUID supplierCompanyId,
	UUID hubId,
	String productName
) {
	public static InternalProductResponse from(Product product) {
		return new InternalProductResponse(
			product.getId(),
			product.getCompanyId(),
			product.getHubId(),
			product.getProductName()
		);
	}
}
