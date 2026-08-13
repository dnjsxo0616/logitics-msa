package com.iftrue.notification.application.dto;

import java.util.UUID;

public record SlackMessageDispatchTarget(
        UUID slackMessageId,
        String receiverId,
        String message
) {
}
