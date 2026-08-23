package com.iftrue.delivery.global.common;

import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String code,
        T data
) {
    public static <T> ApiResponse<T> success(int status, T data) {
        return new ApiResponse<>(Instant.now(), status, "success", data);
    }
}
