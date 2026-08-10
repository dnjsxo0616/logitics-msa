package com.iftrue.order.infrastructure.client.company.dto;

import java.util.UUID;

public record CompanyResponse(
        UUID companyId,
        String companyAddress
) {
}
