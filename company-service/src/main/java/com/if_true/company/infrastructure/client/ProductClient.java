package com.if_true.company.infrastructure.client;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", configuration = ProductClientConfig.class)
public interface ProductClient {

	@GetMapping("/api/v1/internal/products/count")
	long countProducts(@RequestParam UUID companyId);
}
