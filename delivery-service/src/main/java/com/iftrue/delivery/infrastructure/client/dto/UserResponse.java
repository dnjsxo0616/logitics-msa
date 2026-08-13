package com.iftrue.delivery.infrastructure.client.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String slackId
) {
}
