package com.iftrue.user.presentation.response;

import com.iftrue.user.domain.UserRole;
import com.iftrue.user.domain.User;
import com.iftrue.user.domain.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String nickname,
        String email,
        UserRole role,
        UUID hubId,
        UUID companyId,
        UserStatus status
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getRole(),
                user.getHubId(),
                user.getCompanyId(),
                user.getStatus()
        );
    }
}