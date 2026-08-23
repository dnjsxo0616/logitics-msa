package com.iftrue.user.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iftrue.user.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final Instant timestamp;
    private final int status;
    private final String message;
    private final String code;
    private final T data;
    private final List<ValidationError> errors;


    private ApiResponse(String code, int status, String message, T data, List<ValidationError> errors
    ) {
        this.timestamp = Instant.now();
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }


    // =========================
    // SUCCESS
    // =========================
    // 성공 - 데이터 있음
    public static <T> ApiResponse<T> success(HttpStatus status,T data
    ) {
        return new ApiResponse<>(
                "success",
                status.value(),
                null,
                data,
                null
        );
    }


    // 성공 - 데이터 없음
    public static ApiResponse<Void> success(HttpStatus status
    ) {
        return new ApiResponse<>(
                "success",
                status.value(),
                null,
                null,
                null
        );
    }


    // =========================
    // FAIL
    // =========================
    //  실패 (Validation)
    public static ApiResponse<Void> fail( ErrorCode errorCode,List<ValidationError> errors
    ) {
        return new ApiResponse<>(
                errorCode.getCode(),
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                null,
                errors
        );
    }

    //  실패 (business)
    public static ApiResponse<Void> fail(ErrorCode errorCode
    ) {
        return new ApiResponse<>(
                errorCode.getCode(),
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                null,
                null
        );
    }
}
