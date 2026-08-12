package com.if_true.company.presentation.dto;

import com.if_true.company.domain.Company;
import com.if_true.company.domain.CompanyType;
import java.time.Instant;
import java.util.UUID;

public record CompanyResponse(
	UUID id,
	String companyName,
	CompanyType companyType,
	UUID hubId,
	String companyAddress,
	Instant createdAt,
	UUID createdBy,
	Instant updatedAt,
	UUID updatedBy
) {
	public static CompanyResponse from(Company company) {
		return new CompanyResponse(
			company.getId(),
			company.getCompanyName(),
			company.getCompanyType(),
			company.getHubId(),
			company.getCompanyAddress(),
			company.getCreatedAt(),
			company.getCreatedBy(),
			company.getUpdatedAt(),
			company.getUpdatedBy()
		);
	}
}
