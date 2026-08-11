package com.iftrue.user.presentation.request;

import com.iftrue.user.domain.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(

        @NotNull
        UserStatus status

) {
}