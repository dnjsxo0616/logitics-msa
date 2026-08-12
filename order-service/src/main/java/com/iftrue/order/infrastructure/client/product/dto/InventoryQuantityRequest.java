package com.iftrue.order.infrastructure.client.product.dto;

import java.util.UUID;

public record InventoryQuantityRequest(
        UUID orderId,
        int quantity
) {
}
