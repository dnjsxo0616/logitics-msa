package com.iftrue.delivery.infrastructure.client;

import com.iftrue.delivery.global.common.ApiResponse;
import com.iftrue.delivery.infrastructure.client.config.InternalFeignConfig;
import com.iftrue.delivery.infrastructure.client.dto.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service",
        configuration = InternalFeignConfig.class)
public interface CompanyClient {

    @GetMapping("/api/v1/internal/companies/{companyId}")
    ApiResponse<CompanyResponse> getCompany(
            @PathVariable("companyId") UUID companyId
    );
}
