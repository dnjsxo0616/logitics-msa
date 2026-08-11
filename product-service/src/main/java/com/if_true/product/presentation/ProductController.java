package com.if_true.product.presentation;

import com.if_true.product.application.ProductService;
import com.if_true.product.presentation.dto.ProductRequest;
import com.if_true.product.presentation.dto.ProductResponse;
import com.if_true.product.presentation.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
	public ResponseEntity<ProductResponse> create(
		@Valid @RequestBody ProductRequest request
	) {
		ProductResponse response = productService.create(request);
		return ResponseEntity.created(URI.create("/api/v1/products/" + response.id())).body(response);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'SUPPLIER_MANAGER')")
	public ProductResponse get(@PathVariable UUID id) {
		return productService.get(id);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'SUPPLIER_MANAGER')")
	public Page<ProductResponse> search(
		@RequestParam(required = false) String productName,
		@RequestParam(required = false) UUID companyId,
		@RequestParam(required = false) UUID hubId,
		Pageable pageable
	) {
		return productService.search(productName, companyId, hubId, pageable);
	}

	@PatchMapping("/{id}")
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
	public ProductResponse update(
		@PathVariable UUID id,
		@Valid @RequestBody ProductUpdateRequest request
	) {
		return productService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
	public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID actorId) {
		productService.delete(id, actorId);
		return ResponseEntity.noContent().build();
	}
}
