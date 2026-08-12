package com.iftrue.notification.domain.aialert;

import com.iftrue.notification.domain.common.BaseEntity;
import com.iftrue.notification.global.exception.BusinessException;
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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_ai_alert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_payload", nullable = false, columnDefinition = "jsonb")
    private AiRequestPayload requestPayload;

    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "ai_response", columnDefinition = "TEXT")
    private String aiResponse;

    @Column(name = "final_deadline")
    private Instant finalDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @ColumnDefault("'PENDING'")
    private AiAlertStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    @ColumnDefault("0")
    private int retryCount;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    private AiAlert(
            UUID orderId,
            UUID deliveryId,
            AiRequestPayload requestPayload
    ) {
        this.orderId = orderId;
        this.deliveryId = deliveryId;
        this.requestPayload = requestPayload;
        this.status = AiAlertStatus.PENDING;
        this.retryCount = 0;
    }

    public static AiAlert create(
            UUID orderId,
            UUID deliveryId,
            AiRequestPayload requestPayload
    ) {
        validateRequired(orderId);
        validateRequired(deliveryId);
        validateRequired(requestPayload);

        return new AiAlert(orderId, deliveryId, requestPayload);
    }

    public void startProcessing() {
        validateStatus(AiAlertStatus.PENDING, AiAlertStatus.RETRY_WAIT);

        this.status = AiAlertStatus.PROCESSING;
        this.errorMessage = null;
        this.nextRetryAt = null;
    }

    public void complete(
            String prompt,
            String aiResponse,
            Instant finalDeadline
    ) {
        validateStatus(AiAlertStatus.PROCESSING);
        validateText(prompt);
        validateText(aiResponse);
        validateRequired(finalDeadline);

        this.prompt = prompt;
        this.aiResponse = aiResponse;
        this.finalDeadline = finalDeadline;
        this.status = AiAlertStatus.COMPLETED;
        this.errorMessage = null;
        this.nextRetryAt = null;
    }

    public void scheduleRetry(
            String errorMessage,
            Instant nextRetryAt
    ) {
        validateStatus(AiAlertStatus.PROCESSING);
        validateText(errorMessage);
        validateRequired(nextRetryAt);

        this.retryCount++;
        this.errorMessage = errorMessage;
        this.nextRetryAt = nextRetryAt;
        this.status = AiAlertStatus.RETRY_WAIT;
    }

    public void fail(String errorMessage) {
        validateStatus(AiAlertStatus.PROCESSING);
        validateText(errorMessage);

        this.errorMessage = errorMessage;
        this.nextRetryAt = null;
        this.status = AiAlertStatus.FAILED;
    }

    public void cancel() {
        validateStatus(
                AiAlertStatus.PENDING,
                AiAlertStatus.PROCESSING,
                AiAlertStatus.RETRY_WAIT
        );

        this.nextRetryAt = null;
        this.status = AiAlertStatus.CANCELED;
    }

    private void validateStatus(AiAlertStatus... allowedStatuses) {
        boolean allowed = Arrays.stream(allowedStatuses)
                .anyMatch(allowedStatus -> allowedStatus == this.status);

        if (!allowed) {
            throw new BusinessException(NotificationErrorCode.INVALID_NOTIFICATION_STATUS);
        }
    }

    private static void validateRequired(Object value) {
        if (value == null) {
            throw new BusinessException(NotificationErrorCode.INVALID_INPUT);
        }
    }

    private static void validateText(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(NotificationErrorCode.INVALID_INPUT);
        }
    }
}
