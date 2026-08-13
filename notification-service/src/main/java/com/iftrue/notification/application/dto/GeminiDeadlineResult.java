package com.iftrue.notification.application.dto;

import java.time.Instant;

public record GeminiDeadlineResult(
        String prompt,
        String aiResponse,
        Instant finalDeadline
) {
}
