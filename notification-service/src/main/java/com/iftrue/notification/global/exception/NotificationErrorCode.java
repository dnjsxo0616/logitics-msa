package com.iftrue.notification.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "N-001", "알림을 찾을 수 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "N-002", "입력값 검증에 실패했습니다."),
    AI_PROCESSING_FAILED(HttpStatus.BAD_GATEWAY, "N-005", "AI 처리에 실패했습니다."),
    SLACK_MESSAGE_SEND_FAILED(HttpStatus.BAD_GATEWAY, "N-006", "Slack 메시지 발송에 실패했습니다."),
    UNAUTHENTICATED_REQUEST(HttpStatus.UNAUTHORIZED, "N-007", "인증되지 않은 요청입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "N-008", "요청을 처리할 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "N-009", "서버 내부 오류가 발생했습니다."),
    INVALID_NOTIFICATION_STATUS(HttpStatus.CONFLICT, "N-010", "허용되지 않는 알림 상태입니다."),
    AI_PROMPT_TOO_LONG(HttpStatus.PAYLOAD_TOO_LARGE, "N-013", "AI 프롬프트가 허용 길이를 초과했습니다."),
    AI_PROCESSING_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "N-014", "AI 처리 시간이 제한을 초과했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
