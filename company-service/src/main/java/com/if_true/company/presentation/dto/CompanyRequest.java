package com.if_true.company.presentation.dto;

import com.if_true.company.domain.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CompanyRequest(
	@NotBlank
	@Size(max = 100)
	String companyName,

	@NotNull
	CompanyType companyType,

	@NotNull
	UUID hubId,

	@NotBlank
	@Size(max = 255)
	String companyAddress
) {
}
