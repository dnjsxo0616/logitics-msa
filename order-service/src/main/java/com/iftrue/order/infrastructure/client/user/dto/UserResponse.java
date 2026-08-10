package com.iftrue.order.infrastructure.client.user.dto;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        UUID companyId,
        String name,
        String slackId
) {
}
