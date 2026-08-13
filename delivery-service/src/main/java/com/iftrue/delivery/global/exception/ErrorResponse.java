package com.iftrue.delivery.global.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        String code,
        Map<String, Object> details,
        Instant timestamp
) {
    public static ErrorResponse from(
            ErrorCode errorCode,
            Map<String, Object> details
    ) {
        return new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                errorCode.getCode(),
                details == null ? Map.of() : details,
                Instant.now()
        );
    }
}
