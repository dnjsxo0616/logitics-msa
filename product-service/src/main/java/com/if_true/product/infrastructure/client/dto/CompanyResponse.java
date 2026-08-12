package com.if_true.product.infrastructure.client.dto;

import java.time.Instant;
import java.util.UUID;

public record CompanyResponse(
	UUID id,
	String companyName,
	String companyType,
	UUID hubId,
	String companyAddress,
	Instant createdAt,
	UUID createdBy,
	Instant updatedAt,
	UUID updatedBy
) {
}
