package com.iftrue.order.presentation.dto;

import java.util.UUID;

public record OrderCreateResponse(
        UUID orderId,
        UUID deliveryId
) {
}
