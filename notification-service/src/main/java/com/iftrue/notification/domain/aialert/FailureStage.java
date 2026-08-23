package com.iftrue.notification.domain.aialert;

public enum FailureStage {

    /** Gemini API 호출 단계에서 발생한 실패 */
    AI_CALL,

    /** Gemini 응답을 필요한 형식으로 변환하는 단계에서 발생한 실패 */
    AI_RESPONSE_PARSE,

    /** Gemini 응답값의 유효성을 검증하는 단계에서 발생한 실패 */
    AI_VALIDATION,

    /** Slack 메시지를 발송하는 단계에서 발생한 실패 */
    SLACK_SEND
}
