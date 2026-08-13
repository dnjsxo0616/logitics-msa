package com.iftrue.hub.global.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class ErrorResponse {

    private final Instant timestamp;
    private final String code;
    private final int status;
    private final String message;
    private final Map<String, Object> errors;

    private ErrorResponse(
            ErrorCode errorCode,
            Map<String, Object> errors
    ) {
        this.timestamp = Instant.now();
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus().value();
        this.message = errorCode.getMessage();
        this.errors = errors;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode, null);
    }

    public static ErrorResponse of(
            ErrorCode errorCode,
            Map<String, Object> errors
    ) {
        return new ErrorResponse(errorCode, errors);
    }
}
