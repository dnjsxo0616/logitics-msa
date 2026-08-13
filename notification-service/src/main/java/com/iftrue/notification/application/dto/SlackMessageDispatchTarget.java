package com.iftrue.notification.application.dto;

import java.util.UUID;

public record SlackMessageDispatchTarget(
        UUID aiAlertId,
        String receiverId,
        String message
) {
}
