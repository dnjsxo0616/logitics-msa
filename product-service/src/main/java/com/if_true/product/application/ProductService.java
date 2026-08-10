package com.if_true.product.application;

import com.if_true.product.domain.Product;
import com.if_true.product.infrastructure.client.CompanyClient;
import com.if_true.product.infrastructure.ProductRepository;
import com.if_true.product.infrastructure.client.HubClient;
import com.if_true.product.infrastructure.client.dto.CompanyResponse;
import com.if_true.product.presentation.dto.ProductRequest;
import com.if_true.product.presentation.dto.ProductResponse;
import com.if_true.product.presentation.dto.ProductUpdateRequest;
import com.if_true.product.presentation.dto.InventoryResponse;
import com.if_true.product.presentation.dto.InternalProductResponse;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class ProductService {

	private final ProductRepository productRepository;
	private final CompanyClient companyClient;
	private final HubClient hubClient;
	private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
	private final boolean hubValidationEnabled;

	public ProductService(
		ProductRepository productRepository,
		CompanyClient companyClient,
		HubClient hubClient,
		CircuitBreakerFactory<?, ?> circuitBreakerFactory,
		@Value("${msa.validation.hub.enabled:false}") boolean hubValidationEnabled
	) {
		this.productRepository = productRepository;
		this.companyClient = companyClient;
		this.hubClient = hubClient;
		this.circuitBreakerFactory = circuitBreakerFactory;
		this.hubValidationEnabled = hubValidationEnabled;
	}

	@Transactional
	public ProductResponse create(ProductRequest request) {
		CompanyResponse company = validateCompanyExists(request.companyId());
		validateHubExists(request.hubId());
		validateCompanyHub(company, request.hubId());
		Product product = Product.create(
			request.companyId(),
			request.hubId(),
			request.productName(),
			request.productDescription(),
			request.productQuantity()
		);
		return ProductResponse.from(productRepository.save(product));
	}

	public ProductResponse get(UUID id) {
		return ProductResponse.from(findActiveProduct(id));
	}

	public Page<ProductResponse> search(String productName, UUID companyId, UUID hubId, Pageable pageable) {
		Specification<Product> spec = active()
			.and(productNameContains(productName))
			.and(companyIdEquals(companyId))
			.and(hubIdEquals(hubId));
		return productRepository.findAll(spec, pageable).map(ProductResponse::from);
	}

	@Transactional
	public ProductResponse update(UUID id, ProductUpdateRequest request) {
		Product product = findActiveProduct(id);
		UUID nextCompanyId = request.companyId() == null ? product.getCompanyId() : request.companyId();
		UUID nextHubId = request.hubId() == null ? product.getHubId() : request.hubId();
		CompanyResponse company = validateCompanyExists(nextCompanyId);
		validateHubExists(nextHubId);
		validateCompanyHub(company, nextHubId);
		product.update(
			request.companyId(),
			request.hubId(),
			request.productName(),
			request.productDescription(),
			request.productQuantity()
		);
		return ProductResponse.from(product);
	}

	@Transactional
	public void delete(UUID id, UUID actorId) {
		Product product = findActiveProduct(id);
		product.delete(actorId);
	}

	public long countByCompanyId(UUID companyId) {
		return productRepository.countByCompanyIdAndDeletedAtIsNull(companyId);
	}

	public InternalProductResponse getInternalProduct(UUID productId) {
		return InternalProductResponse.from(findActiveProduct(productId));
	}

	public Page<InventoryResponse> searchInventories(String productName, UUID companyId, UUID hubId, Pageable pageable) {
		Specification<Product> spec = active()
			.and(productNameContains(productName))
			.and(companyIdEquals(companyId))
			.and(hubIdEquals(hubId));
		return productRepository.findAll(spec, pageable).map(InventoryResponse::from);
	}

	public InventoryResponse getInventory(UUID productId) {
		return InventoryResponse.from(findActiveProduct(productId));
	}

	@Transactional
	public InventoryResponse adjustInventory(UUID productId, Long productQuantity) {
		Product product = findActiveProduct(productId);
		product.adjustQuantity(productQuantity);
		return InventoryResponse.from(product);
	}

	private Product findActiveProduct(UUID id) {
		return productRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
	}

	private CompanyResponse validateCompanyExists(UUID companyId) {
		return circuitBreakerFactory.create("company-service").run(
			() -> companyClient.getCompany(companyId),
			throwable -> {
				if (throwable instanceof FeignException.NotFound) {
					throw new EntityNotFoundException("Company not found: " + companyId);
				}
				throw new IllegalStateException("Failed to validate company.");
			}
		);
	}

	private void validateHubExists(UUID hubId) {
		if (!hubValidationEnabled) {
			return;
		}
		circuitBreakerFactory.create("hub-service").run(
			() -> hubClient.getHub(hubId),
			throwable -> {
				if (throwable instanceof FeignException.NotFound) {
					throw new EntityNotFoundException("Hub not found: " + hubId);
				}
				throw new IllegalStateException("Failed to validate hub.");
			}
		);
	}

	private void validateCompanyHub(CompanyResponse company, UUID hubId) {
		if (!company.hubId().equals(hubId)) {
			throw new IllegalArgumentException("Product hub must match company hub.");
		}
	}

	private Specification<Product> active() {
		return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
	}

	private Specification<Product> productNameContains(String productName) {
		return (root, query, criteriaBuilder) -> {
			if (!StringUtils.hasText(productName)) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + productName.toLowerCase() + "%");
		};
	}

	private Specification<Product> companyIdEquals(UUID companyId) {
		return (root, query, criteriaBuilder) ->
			companyId == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("companyId"), companyId);
	}

	private Specification<Product> hubIdEquals(UUID hubId) {
		return (root, query, criteriaBuilder) ->
			hubId == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("hubId"), hubId);
	}
}
