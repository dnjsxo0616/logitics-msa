package com.iftrue.order.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private Instant timestamp;
    private int status;
    private String code;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return of(HttpStatus.OK, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return of(HttpStatus.CREATED, data);
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
