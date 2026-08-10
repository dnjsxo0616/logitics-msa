package com.iftrue.user.presentation.response;

import com.iftrue.user.domain.UserStatus;

import java.util.UUID;

public record UserStatusUpdateResponse(
        UUID id,
        UserStatus status,
        java.time.Instant updatedAt
) {
}
