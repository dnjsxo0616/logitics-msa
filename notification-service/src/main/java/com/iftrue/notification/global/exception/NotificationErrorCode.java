package com.iftrue.notification.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "N-001", "입력값 검증에 실패했습니다."),
    ORDER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "N-002", "주문 ID는 필수입니다."),
    DELIVERY_ID_REQUIRED(HttpStatus.BAD_REQUEST, "N-003", "배송 ID는 필수입니다."),
    AI_REQUEST_PAYLOAD_REQUIRED(HttpStatus.BAD_REQUEST, "N-004", "알림 요청 정보는 필수입니다."),
    AI_PROMPT_REQUIRED(HttpStatus.BAD_REQUEST, "N-005", "AI 프롬프트는 필수입니다."),
    AI_RESPONSE_REQUIRED(HttpStatus.BAD_REQUEST, "N-006", "AI 응답은 필수입니다."),
    FINAL_DEADLINE_REQUIRED(HttpStatus.BAD_REQUEST, "N-007", "최종 발송 시한은 필수입니다."),
    FAILURE_STAGE_REQUIRED(HttpStatus.BAD_REQUEST, "N-008", "실패 단계는 필수입니다."),
    FAILURE_MESSAGE_REQUIRED(HttpStatus.BAD_REQUEST, "N-009", "실패 메시지는 필수입니다."),
    INVALID_AI_ALERT_STATUS(HttpStatus.CONFLICT, "N-010", "현재 상태에서는 요청한 작업을 처리할 수 없습니다."),
    INVALID_FAILURE_STAGE(HttpStatus.CONFLICT, "N-011", "현재 상태에서 기록할 수 없는 실패 단계입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "N-012", "서버 오류가 발생했습니다."),
    AI_ALERT_REQUIRED(HttpStatus.BAD_REQUEST, "N-013", "AI 알림 정보는 필수입니다."),
    SLACK_RECEIVER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "N-014", "Slack 수신자 ID는 필수입니다."),
    SLACK_MESSAGE_REQUIRED(HttpStatus.BAD_REQUEST, "N-015", "Slack 메시지는 필수입니다."),
    SLACK_SENT_AT_REQUIRED(HttpStatus.BAD_REQUEST, "N-016", "Slack 발송 시각은 필수입니다."),
    AI_ALERT_NOT_FOUND(HttpStatus.NOT_FOUND, "N-017", "AI 알림을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
