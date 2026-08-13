package com.iftrue.notification.global.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "notification.slack")
public record SlackProperties(
        @NotBlank String botToken,
        @NotNull URI baseUrl,
        @NotNull Duration processingInterval,
        @NotNull Duration processingTimeout,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout
) {

    public SlackProperties {
        validatePositiveDuration(processingInterval, "Slack 처리 주기");
        validatePositiveDuration(processingTimeout, "Slack 처리 제한 시간");
        validatePositiveDuration(connectTimeout, "Slack 연결 제한 시간");
        validatePositiveDuration(readTimeout, "Slack 응답 제한 시간");
    }

    private static void validatePositiveDuration(Duration duration, String name) {
        if (duration != null && (duration.isZero() || duration.isNegative())) {
            throw new IllegalArgumentException(name + "은 0보다 커야 합니다.");
        }
    }
}
