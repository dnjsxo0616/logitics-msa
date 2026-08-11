package com.iftrue.notification.domain.slackmessage;

import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.common.BaseEntity;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_slack_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SlackMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ai_alert_id", nullable = false)
    private AiAlert aiAlert;

    @Column(name = "slack_receiver_id", nullable = false, length = 100)
    private String slackReceiverId;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @ColumnDefault("'WAITING_CONFIRMATION'")
    private SlackMessageStatus status;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    @ColumnDefault("0")
    private int retryCount;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    private SlackMessage(
            AiAlert aiAlert,
            String slackReceiverId,
            String message
    ) {
        this.aiAlert = aiAlert;
        this.slackReceiverId = slackReceiverId;
        this.message = message;
        this.status = SlackMessageStatus.WAITING_CONFIRMATION;
        this.retryCount = 0;
    }

    public static SlackMessage create(
            AiAlert aiAlert,
            String slackReceiverId,
            String message
    ) {
        validateRequired(aiAlert);
        validateText(slackReceiverId);
        validateText(message);

        return new SlackMessage(aiAlert, slackReceiverId, message);
    }

    public void startSending() {
        validateStatus(
                SlackMessageStatus.WAITING_CONFIRMATION,
                SlackMessageStatus.RETRY_WAIT
        );

        this.status = SlackMessageStatus.SENDING;
        this.errorMessage = null;
        this.nextRetryAt = null;
    }

    public void complete(Instant sentAt) {
        validateStatus(SlackMessageStatus.SENDING);
        validateRequired(sentAt);

        this.sentAt = sentAt;
        this.status = SlackMessageStatus.SENT;
        this.errorMessage = null;
        this.nextRetryAt = null;
    }

    public void scheduleRetry(
            String errorMessage,
            Instant nextRetryAt
    ) {
        validateStatus(SlackMessageStatus.SENDING);
        validateText(errorMessage);
        validateRequired(nextRetryAt);

        this.retryCount++;
        this.errorMessage = errorMessage;
        this.nextRetryAt = nextRetryAt;
        this.status = SlackMessageStatus.RETRY_WAIT;
    }

    public void fail(String errorMessage) {
        validateStatus(SlackMessageStatus.SENDING);
        validateText(errorMessage);

        this.errorMessage = errorMessage;
        this.nextRetryAt = null;
        this.status = SlackMessageStatus.FAILED;
    }

    public void cancel() {
        validateStatus(
                SlackMessageStatus.WAITING_CONFIRMATION,
                SlackMessageStatus.SENDING,
                SlackMessageStatus.RETRY_WAIT
        );

        this.nextRetryAt = null;
        this.status = SlackMessageStatus.CANCELED;
    }

    private void validateStatus(SlackMessageStatus... allowedStatuses) {
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
