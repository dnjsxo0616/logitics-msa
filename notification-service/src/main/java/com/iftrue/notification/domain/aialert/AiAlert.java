package com.iftrue.notification.domain.aialert;

import com.iftrue.notification.domain.common.BaseEntity;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.ErrorCode;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_ai_alert")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "delivery_id", nullable = false, unique = true)
    private UUID deliveryId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_payload", nullable = false, columnDefinition = "jsonb")
    private AiRequestPayload requestPayload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AiAlertStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_stage", length = 30)
    private FailureStage failureStage;

    @Column(name = "failure_message", columnDefinition = "text")
    private String failureMessage;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "prompt", columnDefinition = "text")
    private String prompt;

    @Column(name = "ai_response", columnDefinition = "text")
    private String aiResponse;

    @Column(name = "final_deadline")
    private Instant finalDeadline;

    private AiAlert(
            UUID orderId,
            UUID deliveryId,
            AiRequestPayload requestPayload
    ) {
        this.orderId = orderId;
        this.deliveryId = deliveryId;
        this.requestPayload = requestPayload;
        this.status = AiAlertStatus.RECEIVED;
    }

    public static AiAlert create(
            UUID orderId,
            UUID deliveryId,
            AiRequestPayload requestPayload
    ) {
        validateRequired(orderId, NotificationErrorCode.ORDER_ID_REQUIRED);
        validateRequired(deliveryId, NotificationErrorCode.DELIVERY_ID_REQUIRED);
        validateRequired(
                requestPayload,
                NotificationErrorCode.AI_REQUEST_PAYLOAD_REQUIRED
        );

        return new AiAlert(orderId, deliveryId, requestPayload);
    }

    public void completeAi(
            String prompt,
            String aiResponse,
            Instant finalDeadline
    ) {
        validateStatus(AiAlertStatus.RECEIVED);
        validateText(prompt, NotificationErrorCode.AI_PROMPT_REQUIRED);
        validateText(aiResponse, NotificationErrorCode.AI_RESPONSE_REQUIRED);
        validateRequired(
                finalDeadline,
                NotificationErrorCode.FINAL_DEADLINE_REQUIRED
        );

        this.prompt = prompt;
        this.aiResponse = aiResponse;
        this.finalDeadline = finalDeadline;
        this.status = AiAlertStatus.AI_COMPLETED;

        clearFailure();
    }

    public void completeSlack() {
        validateStatus(AiAlertStatus.AI_COMPLETED);

        this.status = AiAlertStatus.SENT;
        clearFailure();
    }

    public void recordFailure(
            FailureStage stage,
            String message
    ) {
        validateRequired(stage, NotificationErrorCode.FAILURE_STAGE_REQUIRED);
        validateFailureStage(stage);
        validateText(message, NotificationErrorCode.FAILURE_MESSAGE_REQUIRED);

        this.failureStage = stage;
        this.failureMessage = message;
        this.failedAt = Instant.now();
    }

    private void validateFailureStage(FailureStage stage) {
        boolean allowed = switch (status) {
            case RECEIVED ->
                    stage == FailureStage.AI_CALL
                            || stage == FailureStage.AI_RESPONSE_PARSE
                            || stage == FailureStage.AI_VALIDATION;
            case AI_COMPLETED -> stage == FailureStage.SLACK_SEND;
            case SENT -> false;
        };

        if (!allowed) {
            throw new BusinessException(
                    NotificationErrorCode.INVALID_FAILURE_STAGE
            );
        }
    }

    private void validateStatus(AiAlertStatus expectedStatus) {
        if (status != expectedStatus) {
            throw new BusinessException(
                    NotificationErrorCode.INVALID_AI_ALERT_STATUS
            );
        }
    }

    private void clearFailure() {
        this.failureStage = null;
        this.failureMessage = null;
        this.failedAt = null;
    }

    private static void validateText(
            String value,
            ErrorCode errorCode
    ) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(errorCode);
        }
    }

    private static void validateRequired(
            Object value,
            ErrorCode errorCode
    ) {
        if (value == null) {
            throw new BusinessException(errorCode);
        }
    }
}
