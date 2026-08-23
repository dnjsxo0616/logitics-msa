package com.iftrue.notification.global.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.LocalTime;
import java.time.ZoneId;

@Validated
@ConfigurationProperties(prefix = "notification")
public record NotificationProperties(
        @NotNull
        ZoneId businessZone,

        @NotNull
        LocalTime workStartTime,

        @NotNull
        LocalTime workEndTime
) {
}
