package com.if_true.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

	@Column(name = "company_id", nullable = false)
	private UUID companyId;

	@Column(name = "hub_id", nullable = false)
	private UUID hubId;

	@Column(name = "product_name", nullable = false, length = 100)
	private String productName;

	@Column(name = "product_description", columnDefinition = "text")
	private String productDescription;

	@Column(name = "product_quantity", nullable = false)
	private Long productQuantity;

	private Product(UUID companyId, UUID hubId, String productName, String productDescription, Long productQuantity) {
		validateQuantity(productQuantity);
		this.companyId = companyId;
		this.hubId = hubId;
		this.productName = productName;
		this.productDescription = productDescription;
		this.productQuantity = productQuantity;
	}

	public static Product create(UUID companyId, UUID hubId, String productName, String productDescription, Long productQuantity) {
		return new Product(companyId, hubId, productName, productDescription, productQuantity);
	}

	public void update(UUID companyId, UUID hubId, String productName, String productDescription, Long productQuantity) {
		if (companyId != null) {
			this.companyId = companyId;
		}
		if (hubId != null) {
			this.hubId = hubId;
		}
		if (productName != null) {
			this.productName = productName;
		}
		if (productDescription != null) {
			this.productDescription = productDescription;
		}
		if (productQuantity != null) {
			validateQuantity(productQuantity);
			this.productQuantity = productQuantity;
		}
	}

	public void delete(UUID actorId) {
		markDeleted(actorId);
	}

	public void adjustQuantity(Long productQuantity) {
		validateQuantity(productQuantity);
		this.productQuantity = productQuantity;
	}

	public void decreaseQuantity(Long quantity) {
		validateQuantity(quantity);
		if (productQuantity < quantity) {
			throw new IllegalStateException("Product quantity is insufficient.");
		}
		this.productQuantity -= quantity;
	}

	public void restoreQuantity(Long quantity) {
		validateQuantity(quantity);
		this.productQuantity += quantity;
	}

	private static void validateQuantity(Long quantity) {
		if (quantity == null || quantity < 0) {
			throw new IllegalArgumentException("Product quantity must be greater than or equal to 0.");
		}
	}
}
