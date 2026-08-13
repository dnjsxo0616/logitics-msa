package com.iftrue.user.presentation.response;


import com.iftrue.user.domain.UserRole;
import com.iftrue.user.domain.UserStatus;

import java.util.UUID;

public record InternalUserResponse(
        UUID id,
        String username,
        String nickname,
        String email,
        String slackId,
        UserRole role,
        UUID hubId,
        UUID companyId,
        UserStatus status
) {
}