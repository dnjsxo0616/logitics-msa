package com.iftrue.notification.infrastructure.client.slack.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SlackPostMessageResponse(
        boolean ok,
        String error,
        String channel,
        String ts
) {
}
