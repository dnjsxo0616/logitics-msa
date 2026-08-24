package com.iftrue.notification.global.response;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String code,
        T data
) {

    public static <T> ApiResponse<T> success(T data) {
        return of(HttpStatus.OK, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return of(HttpStatus.CREATED, data);
    }

    public static <T> ApiResponse<T> accepted(T data) {
        return of(HttpStatus.ACCEPTED, data);
    }

    private static <T> ApiResponse<T> of(HttpStatus status, T data) {
        return new ApiResponse<>(
                Instant.now(),
                status.value(),
                "success",
                data
        );
    }
}
