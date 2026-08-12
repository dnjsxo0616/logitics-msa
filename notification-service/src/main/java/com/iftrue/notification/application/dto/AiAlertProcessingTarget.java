package com.iftrue.notification.application.dto;

import com.iftrue.notification.domain.aialert.AiRequestPayload;

import java.util.UUID;

public record AiAlertProcessingTarget(
        UUID aiAlertId,
        AiRequestPayload requestPayload
) {
}
