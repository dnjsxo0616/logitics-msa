package com.if_true.product.presentation;

import com.if_true.product.application.ProductService;
import com.if_true.product.presentation.dto.InventoryAdjustRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/inventories")
@RequiredArgsConstructor
public class InternalInventoryController {

	private final ProductService productService;

	@PostMapping("/{productId}/restore")
	public ResponseEntity<Void> restore(
		@PathVariable UUID productId,
		@Valid @RequestBody InventoryAdjustRequest request
	) {
		productService.restoreInventory(productId, request.quantity());
		return ResponseEntity.noContent().build();
	}
}
