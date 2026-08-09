package com.if_true.product.presentation;

import com.if_true.product.application.ProductService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/products")
@RequiredArgsConstructor
public class InternalProductController {

	private final ProductService productService;

	@GetMapping("/count")
	public long count(@RequestParam UUID companyId) {
		return productService.countByCompanyId(companyId);
	}
}
