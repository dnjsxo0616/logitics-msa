package com.iftrue.notification.domain.aialert;

import com.iftrue.notification.domain.common.BaseEntity;
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
import java.util.Objects;
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
    @Column(name = "delivery_payload", nullable = false, columnDefinition = "jsonb")
    private DeliveryPayload deliveryPayload;

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
            DeliveryPayload deliveryPayload
    ) {
        this.orderId = Objects.requireNonNull(orderId, "주문 ID는 필수입니다.");
        this.deliveryId = Objects.requireNonNull(deliveryId, "배송 ID는 필수입니다.");
        this.deliveryPayload = Objects.requireNonNull(deliveryPayload, "배송 정보는 필수입니다.");
        this.status = AiAlertStatus.PENDING;
        this.retryCount = 0;
    }

    public static AiAlert create(
            UUID orderId,
            UUID deliveryId,
            DeliveryPayload deliveryPayload
    ) {
        return new AiAlert(orderId, deliveryId, deliveryPayload);
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

        this.prompt = requireText(prompt, "AI 프롬프트는 필수입니다.");
        this.aiResponse = requireText(aiResponse, "AI 응답은 필수입니다.");
        this.finalDeadline = Objects.requireNonNull(finalDeadline, "최종 발송 시한은 필수입니다.");
        this.status = AiAlertStatus.COMPLETED;
        this.errorMessage = null;
        this.nextRetryAt = null;
    }

    public void markRetry(
            String errorMessage,
            Instant nextRetryAt
    ) {
        validateStatus(AiAlertStatus.PROCESSING);

        this.retryCount++;
        this.errorMessage = requireText(errorMessage, "오류 내용은 필수입니다.");
        this.nextRetryAt = Objects.requireNonNull(nextRetryAt, "다음 재시도 시각은 필수입니다.");
        this.status = AiAlertStatus.RETRY_WAIT;
    }

    public void fail(String errorMessage) {
        validateStatus(AiAlertStatus.PROCESSING);

        this.retryCount++;
        this.errorMessage = requireText(errorMessage, "오류 내용은 필수입니다.");
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
            throw new IllegalStateException(
                    "현재 AI 알림 상태에서는 요청한 작업을 수행할 수 없습니다. status=" + status
            );
        }
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }
}
