package com.if_true.company.presentation.dto;

import com.if_true.company.domain.Company;
import java.util.UUID;

public record InternalCompanyResponse(
	UUID id,
	String companyName,
	String companyType,
	UUID hubId,
	String companyAddress
) {
	public static InternalCompanyResponse from(Company company) {
		return new InternalCompanyResponse(
			company.getId(),
			company.getCompanyName(),
			company.getCompanyType().name(),
			company.getHubId(),
			company.getCompanyAddress()
		);
	}
}
