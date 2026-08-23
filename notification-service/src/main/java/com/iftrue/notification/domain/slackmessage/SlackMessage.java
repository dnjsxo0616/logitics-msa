package com.iftrue.notification.domain.slackmessage;

import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.common.BaseEntity;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.ErrorCode;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_slack_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SlackMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_alert_id")
    private AiAlert aiAlert;

    @Column(name = "slack_receiver_id", nullable = false)
    private String slackReceiverId;

    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    private SlackMessage(
            AiAlert aiAlert,
            String slackReceiverId,
            String message,
            Instant sentAt
    ) {
        this.aiAlert = aiAlert;
        this.slackReceiverId = slackReceiverId;
        this.message = message;
        this.sentAt = sentAt;
    }

    public static SlackMessage createForAiAlert(
            AiAlert aiAlert,
            String receiverId,
            String message,
            Instant sentAt
    ) {
        validateRequired(aiAlert, NotificationErrorCode.AI_ALERT_REQUIRED);
        validateText(
                receiverId,
                NotificationErrorCode.SLACK_RECEIVER_ID_REQUIRED
        );
        validateText(message, NotificationErrorCode.SLACK_MESSAGE_REQUIRED);
        validateRequired(
                sentAt,
                NotificationErrorCode.SLACK_SENT_AT_REQUIRED
        );

        return new SlackMessage(aiAlert, receiverId, message, sentAt);
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
