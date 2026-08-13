package com.if_true.product.infrastructure.client;

import com.if_true.product.infrastructure.client.dto.CompanyResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "company-service")
public interface CompanyClient {

	@GetMapping("/api/v1/companies/{companyId}")
	CompanyResponse getCompany(
		@PathVariable UUID companyId,
		@RequestHeader("X-Gateway-Secret") String gatewaySecret,
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole
	);
}
