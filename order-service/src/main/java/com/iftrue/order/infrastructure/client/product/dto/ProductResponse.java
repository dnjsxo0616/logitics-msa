package com.iftrue.order.infrastructure.client.product.dto;

import java.util.UUID;

public record ProductResponse(
        UUID productId,
        UUID companyId,
        String productName
) {
}
