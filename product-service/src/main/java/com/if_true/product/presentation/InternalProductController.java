package com.if_true.product.presentation;

import com.if_true.product.application.ProductService;
import com.if_true.product.presentation.dto.InternalProductResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/products")
@RequiredArgsConstructor
public class InternalProductController {

	private final ProductService productService;

	@GetMapping("/{productId}")
	public InternalProductResponse get(@PathVariable UUID productId) {
		return productService.getInternalProduct(productId);
	}

	@GetMapping("/count")
	public long count(@RequestParam UUID companyId) {
		return productService.countByCompanyId(companyId);
	}
}
