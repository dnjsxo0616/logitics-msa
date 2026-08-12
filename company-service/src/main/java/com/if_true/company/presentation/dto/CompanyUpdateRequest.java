package com.if_true.company.presentation.dto;

import com.if_true.company.domain.CompanyType;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CompanyUpdateRequest(
	@Size(max = 100)
	String companyName,
	CompanyType companyType,
	UUID hubId,
	@Size(max = 255)
	String companyAddress
) {
}
