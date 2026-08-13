package com.iftrue.user.global.exception;


import com.iftrue.user.global.response.ApiResponse;
import com.iftrue.user.global.response.ValidationError;
import feign.FeignException;
import org.springframework.http.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * @Valid DTO 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e
    ) {

        List<ValidationError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(
                        ErrorCode.INVALID_INPUT_VALUE,
                        errors
                ));
    }


    /**
     * Business Exception
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException e
    ) {

        ErrorCode errorCode = e.getErrorCode();


        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }


    /**
     * IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException e
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(
                        ErrorCode.INVALID_INPUT_VALUE
                ));
    }


    /**
     * 예상하지 못한 Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception e
    ) {

        log.error("Unhandled Exception", e);


        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(
                        ErrorCode.INTERNAL_SERVER_ERROR
                ));
    }

    /**
     * Feign Client Exception
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(
            FeignException e
    ) {

        log.error("Feign Client Error: status={}, message={}",
                e.status(), e.getMessage());

        return ResponseEntity
                .status(e.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.contentUTF8());
    }


}