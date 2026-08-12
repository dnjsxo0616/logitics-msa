package com.iftrue.notification.global.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;

@Validated
@ConfigurationProperties(prefix = "notification.ai")
public record AiDeadlineProperties(
        @NotBlank String timezone,
        @NotNull LocalTime workStart,
        @NotNull LocalTime workEnd,
        @Positive int maxPromptChars,
        @NotNull Duration processingInterval,
        @NotNull Duration processingTimeout
) {

    public AiDeadlineProperties {
        ZoneId.of(timezone);

        if (workStart != null
                && workEnd != null
                && !workStart.isBefore(workEnd)) {
            throw new IllegalArgumentException("AI 근무 시작 시각은 종료 시각보다 빨라야 합니다.");
        }

        validatePositiveDuration(processingInterval, "AI 처리 주기");
        validatePositiveDuration(processingTimeout, "AI 처리 제한 시간");
    }

    public ZoneId zoneId() {
        return ZoneId.of(timezone);
    }

    private static void validatePositiveDuration(Duration duration, String name) {
        if (duration != null && (duration.isZero() || duration.isNegative())) {
            throw new IllegalArgumentException(name + "은 0보다 커야 합니다.");
        }
    }
}
