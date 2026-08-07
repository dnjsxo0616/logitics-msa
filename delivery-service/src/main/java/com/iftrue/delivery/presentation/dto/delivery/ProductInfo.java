package com.iftrue.delivery.presentation.dto.delivery;


import java.util.UUID;

public record ProductInfo(
        UUID productId,
        String name,
        int quantity
) {
}
