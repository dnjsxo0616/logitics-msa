package com.iftrue.delivery.infrastructure.client.dto;

import java.util.UUID;

public record CompanyResponse(
        UUID id,
        UUID hubId,
        String companyAddress
) {
}
