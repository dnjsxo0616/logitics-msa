package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.SlackMessageDispatchTarget;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import com.iftrue.notification.infrastructure.client.slack.SlackClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackMessageProcessor {

    private final SlackMessageTransactionService transactionService;
    private final SlackClient slackClient;

    @Scheduled(fixedDelayString = "${notification.slack.processing-interval}")
    public void processNext() {
        transactionService.recoverTimedOut();
        claimNext().ifPresent(this::send);
    }

    public void processForAiAlert(UUID aiAlertId) {
        claimForAiAlert(aiAlertId).ifPresent(this::send);
    }

    private Optional<SlackMessageDispatchTarget> claimNext() {
        try {
            return transactionService.claimNext();
        } catch (RuntimeException exception) {
            log.error("[Slack-Message] 메시지 생성/선점에 실패했습니다.", exception);
            return Optional.empty();
        }
    }

    private Optional<SlackMessageDispatchTarget> claimForAiAlert(UUID aiAlertId) {
        try {
            return transactionService.claimForAiAlert(aiAlertId);
        } catch (RuntimeException exception) {
            log.error("[Slack-Message] 메시지 생성/선점에 실패했습니다. aiAlertId={}", aiAlertId, exception);
            return Optional.empty();
        }
    }

    private void send(SlackMessageDispatchTarget target) {
        try {
            slackClient.sendMessage(target.receiverId(), target.message());
        } catch (RuntimeException exception) {
            transactionService.fail(
                    target.slackMessageId(),
                    getErrorMessage(exception)
            );
            return;
        }

        transactionService.complete(target.slackMessageId(), Instant.now());
    }

    private String getErrorMessage(RuntimeException exception) {
        String detail = exception.getMessage();
        String prefix = NotificationErrorCode.SLACK_MESSAGE_SEND_FAILED.getCode()
                + ": "
                + NotificationErrorCode.SLACK_MESSAGE_SEND_FAILED.getMessage();

        if (detail == null || detail.isBlank()) {
            return prefix;
        }

        return prefix + " (" + detail + ")";
    }
}
