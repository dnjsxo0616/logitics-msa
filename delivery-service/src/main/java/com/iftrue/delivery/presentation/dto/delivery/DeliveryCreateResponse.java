package com.iftrue.delivery.presentation.dto.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateResult;

import java.util.UUID;

public record DeliveryCreateResponse(
        UUID deliveryId
) {

    public static DeliveryCreateResponse from(DeliveryCreateResult deliveryCreateResult) {
        return new DeliveryCreateResponse(deliveryCreateResult.deliveryId());
    }
}
