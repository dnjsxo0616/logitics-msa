package com.iftrue.notification.infrastructure.client.slack.dto;

public record SlackPostMessageRequest(
        String channel,
        String text
) {
}
