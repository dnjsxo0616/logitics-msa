package com.iftrue.delivery.global.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeliveryServiceException.class)
    public ResponseEntity<ErrorResponse> handleDeliveryServiceException(DeliveryServiceException e) {
        log.error("[ItsHereException] = {}", e.getMessage());
        ErrorCode code = e.getErrorCode();
        Map<String, Object> details = e.getDetails() != null && !e.getDetails().isEmpty()
                ? e.getDetails()
                : Map.of("reason", e.getMessage());

        return ResponseEntity.status(code.getStatus())
                .body(ErrorResponse.from(code, details));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("[IllegalStateException] = {}", e.getMessage());
        ErrorCode code = ErrorCode.INVALID_STATE;

        return createResponse(
                code,
                Map.of("reason", e.getMessage())
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        log.error("[NoSuchElementException] = {}", e.getMessage());
        ErrorCode code = ErrorCode.NOT_FOUND;

        return createResponse(
                code,
                Map.of("reason", e.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[Exception] = {}", e.getMessage());
        log.error("[Exception] = {}", e.getClass().getSimpleName());
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        return createResponse(
                code,
                Map.of()
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        log.error("필수 파트 누락: {}", e.getRequestPartName());
        ErrorCode code = ErrorCode.INVALID_INPUT_VALUE;

        return createResponse(
                code,
                Map.of()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("필수 쿼리 파라미터 누락: {}", e.getParameterName());
        ErrorCode code = ErrorCode.INVALID_INPUT_VALUE;

        return createResponse(
                code,
                Map.of()
        );
    }

//    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class}) // TODO: 추후 Security 생길때 추가
//    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception e) {
//        ErrorCode code = ErrorCode.AUTH_FORBIDDEN;
//
//        return createResponse(
//                code,
//                Map.of()
//        );
//    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("[IllegalArgumentException] = {}", e.getMessage());
        ErrorCode code = ErrorCode.INVALID_REQUEST;

        return createResponse(
                code,
                Map.of("reason", e.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMisMatchException(MethodArgumentTypeMismatchException e) {
        log.error("[MethodArgumentTypeMismatchException] = {}", e.getMessage());

        ErrorCode code = ErrorCode.INVALID_REQUEST;

        return createResponse(
                code,
                Map.of("reason", e.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("[MethodArgumentNotValidException] = {}", e.getMessage());
        Map<String, Object> details = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage()
                                : "Invalid input",
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        ErrorCode code = ErrorCode.INVALID_INPUT_VALUE;

        return createResponse(
                code,
                details
        );

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[HttpMessageNotReadableException] = {}", e.getMessage());
        ErrorCode code = ErrorCode.INVALID_REQUEST;

        return createResponse(
                code,
                Map.of()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("[HttpMediaTypeNotSupportedException] = {}", e.getMessage());
        ErrorCode code = ErrorCode.UNSUPPORTED_MEDIA_TYPE;

        return createResponse(
                code,
                Map.of("reason", e.getMessage())
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("[BindException] = {}", e.getMessage());
        Map<String, Object> details = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid input",
                        (a, b) -> a, // 같은 필드 중복시 첫 값 유지
                        LinkedHashMap::new
                ));
        ErrorCode code = ErrorCode.INVALID_INPUT_VALUE;

        return createResponse(code, details);
    }

    private ResponseEntity<ErrorResponse> createResponse(
            ErrorCode code,
            Map<String, Object> details
    ) {
        return ResponseEntity.status(code.getStatus())
                .body(ErrorResponse.from(code, details));
    }
}
