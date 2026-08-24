package com.iftrue.notification.application.dto;

import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertStatus;

public record NotificationCreateResult(
        AiAlert aiAlert,
        boolean created
) {

    public boolean completed() {
        return aiAlert.getStatus() == AiAlertStatus.SENT;
    }
}
