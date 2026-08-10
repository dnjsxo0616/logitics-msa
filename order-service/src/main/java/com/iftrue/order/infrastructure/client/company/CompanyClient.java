package com.iftrue.order.infrastructure.client.company;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    // 협의 필요한 api
    @GetMapping("/api/v1/internal/companies/{companyId}")
    void checkCompanyExists(@PathVariable("companyId") UUID companyId);
}
