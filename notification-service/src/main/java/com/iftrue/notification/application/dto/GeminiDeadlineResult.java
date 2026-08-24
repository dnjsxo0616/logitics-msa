package com.iftrue.notification.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiDeadlineResult(
        OffsetDateTime finalDeadline,
        String reason
) {
}
