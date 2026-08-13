package com.iftrue.delivery.infrastructure.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HubResponse(
        UUID id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
