package com.iftrue.notification.global.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(
        String apiKey,

        @NotBlank
        String model,

        @NotBlank
        String baseUrl,

        @NotNull
        Duration connectTimeout,

        @NotNull
        Duration readTimeout
) {
}
