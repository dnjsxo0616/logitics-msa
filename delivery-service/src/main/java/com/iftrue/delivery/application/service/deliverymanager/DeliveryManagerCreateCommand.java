package com.iftrue.delivery.application.service.deliverymanager;

import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerCreateCommand(
        UUID userId,
        String slackId,
        UUID hubId,
        DeliveryManagerType type
) {
}
