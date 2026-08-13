package com.iftrue.order.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private Instant timestamp;
    private int status;
    private String code;
    private String message;
    private Map<String, Object> errors;

    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, Collections.emptyMap());
    }

    public static ErrorResponse of(
            ErrorCode errorCode,
            Map<String, Object> errors
    ) {
        return new ErrorResponse(
                Instant.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                errors
        );
    }
}
