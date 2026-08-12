package com.iftrue.gateway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // =========================
    // AUTH
    // =========================
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "G-001", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "G-003", "유효하지 않은 토큰입니다."),

    // =========================
    // GATEWAY
    // =========================
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "G-004", "요청한 경로를 찾을 수 없습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "G-005", "서비스를 사용할 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G-999", "서버 내부 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}