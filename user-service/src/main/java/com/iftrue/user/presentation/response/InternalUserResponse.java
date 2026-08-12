package com.iftrue.user.presentation.response;


import java.util.UUID;

public record InternalUserResponse(
        UUID companyId,
        String name,
        String email,
        String slackId
) {
}