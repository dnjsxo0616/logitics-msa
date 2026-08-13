package com.if_true.company.presentation;

import com.if_true.company.application.CompanyService;
import com.if_true.company.global.response.ApiResponse;
import com.if_true.company.presentation.dto.InternalCompanyResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/companies")
public class InternalCompanyController {

	private final CompanyService companyService;

	public InternalCompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@GetMapping("/{companyId}")
	public ApiResponse<InternalCompanyResponse> getCompany(@PathVariable UUID companyId) {
		return ApiResponse.success(companyService.getInternalCompany(companyId));
	}
}
