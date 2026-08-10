package com.iftrue.hub.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 허브를 찾을 수 없습니다.", "H-001"),
    HUB_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 허브 이름입니다.", "H-002"),

    HUB_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 허브 경로를 찾을 수 없습니다.", "H-003"),
    HUB_ROUTE_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 허브 경로입니다.", "H-004"),
    HUB_ROUTE_SAME_ENDPOINT(HttpStatus.BAD_REQUEST, "출발 허브와 도착 허브는 같을 수 없습니다.", "H-005"),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다.", "H-006"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 요청입니다.", "H-007"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", "H-008"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", "H-009"),

    EXTERNAL_ROUTE_API_ERROR(HttpStatus.BAD_GATEWAY, "경로 정보를 조회하지 못했습니다.", "H-010");

    private final HttpStatus status;
    private final String message;
    private final String code;

    ErrorCode(HttpStatus status, String message, String code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
