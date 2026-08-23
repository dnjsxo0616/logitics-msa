package com.iftrue.order.infrastructure.client.company;

import com.iftrue.order.global.response.ApiResponse;
import com.iftrue.order.infrastructure.client.company.dto.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/v1/internal/companies/{companyId}")
    ApiResponse<CompanyResponse> getCompany(
            @PathVariable("companyId") UUID companyId
    );
}
