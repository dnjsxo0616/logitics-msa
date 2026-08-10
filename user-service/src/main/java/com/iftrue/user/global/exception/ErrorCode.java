package com.iftrue.user.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-001", "사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "U-002", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U-003", "비밀번호가 일치하지 않습니다."),
    USERNAME_DUPLICATED(HttpStatus.CONFLICT, "U-004", "이미 사용 중인 아이디입니다."),
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "U-005", "이미 사용 중인 닉네임입니다."),
    INVALID_USER_STATUS(HttpStatus.BAD_REQUEST, "U-006", "현재 상태에서는 변경할 수 없습니다."),
    USER_REJECT_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "U-007", "거절 사유는 필수입니다."),
    DUPLICATED_RESOURCE(HttpStatus.CONFLICT, "U-008", "이미 사용 중인 정보입니다."),
    ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "U-009", "이미 탈퇴한 회원입니다."),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "U-010", "기존 비밀번호와 동일합니다."),

    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "U-011", "인증이 필요합니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "U-012", "만료된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "U-013", "재발급이 불가능합니다. 다시 로그인해주세요."),
    INVALID_ID_PASSWORD(HttpStatus.UNAUTHORIZED, "U-014", "아이디 및 비밀번호가 틀렸습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "U-015", "인증 토큰이 유효하지 않습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "U-016", "이메일 또는 비밀번호가 올바르지 않습니다."),

    USER_NOT_APPROVED(HttpStatus.FORBIDDEN, "U-017", "승인되지 않은 사용자입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "U-018", "잘못된 입력값입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "U-019", "접근 권한이 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "U-020", "요청한 리소스를 찾을 수 없습니다."),
    BUSINESS_RULE_VIOLATION(HttpStatus.CONFLICT, "U-021", "비즈니스 규칙에 위배되는 요청입니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "U-500", "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
