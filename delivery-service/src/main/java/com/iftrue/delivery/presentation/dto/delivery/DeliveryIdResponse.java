package com.iftrue.delivery.presentation.dto.delivery;

import java.util.UUID;

public record DeliveryIdResponse(
        UUID deliveryId
) {
    public static DeliveryIdResponse of(UUID deliveryId) {
        return new DeliveryIdResponse(deliveryId);
    }
}
