package com.if_true.product.presentation.dto;

import com.if_true.product.domain.Product;
import java.util.UUID;

public record InventoryResponse(
	UUID productId,
	String productName,
	UUID companyId,
	UUID hubId,
	Long productQuantity
) {
	public static InventoryResponse from(Product product) {
		return new InventoryResponse(
			product.getId(),
			product.getProductName(),
			product.getCompanyId(),
			product.getHubId(),
			product.getProductQuantity()
		);
	}
}
