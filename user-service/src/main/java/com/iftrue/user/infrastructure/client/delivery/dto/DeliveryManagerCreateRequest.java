package com.iftrue.user.infrastructure.client.delivery.dto;

import com.iftrue.user.infrastructure.client.delivery.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerCreateRequest(
        UUID userId,
        String slackId,
        UUID hubId,
        DeliveryManagerType type
) {
}
