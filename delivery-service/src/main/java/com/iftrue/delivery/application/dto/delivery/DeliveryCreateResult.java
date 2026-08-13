package com.iftrue.delivery.application.dto.delivery;

import java.util.UUID;

public record DeliveryCreateResult(
        UUID deliveryId
) {
    public static DeliveryCreateResult from(CreatedDelivery createdDelivery) {
        return new DeliveryCreateResult(createdDelivery.deliveryId());
    }
}
