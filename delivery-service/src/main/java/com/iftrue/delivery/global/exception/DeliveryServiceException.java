package com.iftrue.delivery.global.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class DeliveryServiceException extends RuntimeException {
    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DeliveryServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Map.of();
    }

    public DeliveryServiceException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details == null ? Map.of() : Map.copyOf(details);
    }

    public DeliveryServiceException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = timestamp;
        this.errorCode = errorCode;
        this.details = details;
    }
}
