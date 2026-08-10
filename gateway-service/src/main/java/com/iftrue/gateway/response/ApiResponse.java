package com.iftrue.gateway.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iftrue.gateway.exception.ErrorCode;
import lombok.Getter;

import java.time.OffsetDateTime;



@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private final OffsetDateTime timestamp;
    private final int status;
    private final String message;
    private final String code;

    private ApiResponse(
            String code,
            int status,
            String message
    ) {
        this.timestamp = OffsetDateTime.now();
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public static ApiResponse fail(ErrorCode errorCode) {
        return new ApiResponse(
                errorCode.getCode(),
                errorCode.getStatus().value(),
                errorCode.getMessage()
        );
    }
}