package com.iftrue.order.infrastructure.client.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserResponse(
        @NotNull UUID companyId,
        @NotBlank String nickname,
        @NotBlank @Email String email,
        @NotBlank String slackId
) {
}
