package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.SlackMessageDispatchTarget;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import com.iftrue.notification.infrastructure.client.slack.SlackClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void processForAiAlert(UUID aiAlertId) {
        prepare(aiAlertId).ifPresent(this::send);
    }

    private Optional<SlackMessageDispatchTarget> prepare(UUID aiAlertId) {
        try {
            return transactionService.prepare(aiAlertId);
        } catch (RuntimeException exception) {
            log.error("[Slack-Message] 메시지 준비에 실패했습니다. aiAlertId={}", aiAlertId, exception);
            return Optional.empty();
        }
    }

    private void send(SlackMessageDispatchTarget target) {
        try {
            slackClient.sendMessage(target.receiverId(), target.message());
        } catch (RuntimeException exception) {
            saveFail(target, getErrorMessage(exception));
            return;
        }

        saveComplete(target);
    }

    private void saveComplete(SlackMessageDispatchTarget target) {
        try {
            transactionService.complete(target.aiAlertId(), target.receiverId(), target.message(), Instant.now());
        } catch (RuntimeException exception) {
            log.error("[Slack-Message] 발송 결과 저장에 실패했습니다. aiAlertId={}", target.aiAlertId(), exception);
        }
    }

    private void saveFail(SlackMessageDispatchTarget target, String errorMessage) {
        try {
            transactionService.fail(target.aiAlertId(), target.receiverId(), target.message(), errorMessage);
        } catch (RuntimeException exception) {
            log.error("[Slack-Message] 발송 결과 저장에 실패했습니다. aiAlertId={}", target.aiAlertId(), exception);
        }
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
