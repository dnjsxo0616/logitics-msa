package com.iftrue.delivery.global.common;

import java.time.Instant;

public record ApiResponse<T>(
        int status,
        String code,
        T data,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(int status, T data) {
        return new ApiResponse<>(status, "success", data, Instant.now());
    }
}
