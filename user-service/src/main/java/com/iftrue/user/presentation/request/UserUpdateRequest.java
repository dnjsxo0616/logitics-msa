package com.iftrue.user.presentation.request;

public record UserUpdateRequest(

        String username,
        String nickname,
        String email

) {
}
