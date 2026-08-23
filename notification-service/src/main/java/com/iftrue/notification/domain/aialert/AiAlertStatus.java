package com.iftrue.notification.domain.aialert;

public enum AiAlertStatus {

    /** 알림 요청을 전달받아 최초 데이터를 저장한 상태 */
    RECEIVED,

    /** Gemini 응답 처리와 최종 발송 시한 계산을 완료한 상태 */
    AI_COMPLETED,

    /** Slack 메시지 발송까지 완료한 상태 */
    SENT
}
