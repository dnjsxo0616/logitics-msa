package com.if_true.product.infrastructure.client;

import com.if_true.product.infrastructure.client.dto.CompanyResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyClient {

	@GetMapping("/api/v1/companies/{id}")
	CompanyResponse getCompany(@PathVariable UUID id);
}
