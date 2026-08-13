package com.iftrue.notification.application.service;

import com.iftrue.notification.domain.aialert.AiRequestPayload;
import com.iftrue.notification.global.config.AiDeadlineProperties;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiDeadlinePromptFactory {

    private final AiDeadlineProperties properties;

    public String create(AiRequestPayload request) {
        if (request == null) {
            throw new BusinessException(NotificationErrorCode.INVALID_INPUT);
        }

        int routeMinutes = request.transitHubs().stream()
                .mapToInt(AiRequestPayload.TransitHub::expectedDurationMinutes)
                .sum();
        String localArrival = request.requestedArrivalAt()
                .atZone(properties.zoneId())
                .toOffsetDateTime()
                .toString();

        String prompt = ("Return JSON only: {\"finalDeadline\":\"ISO-8601 with offset\"}. "
                + "Timezone=%s, work every day=%s-%s. Arrival in this timezone=%s. "
                + "Subtract exactly %d minutes (minutes, not hours or days) within work hours. "
                + "If subtraction crosses work start, continue from previous day work end. No buffer.")
                .formatted(
                        properties.timezone(),
                        properties.workStart(),
                        properties.workEnd(),
                        localArrival,
                        routeMinutes
                );

        if (prompt.length() > properties.maxPromptChars()) {
            throw new BusinessException(NotificationErrorCode.AI_PROMPT_TOO_LONG);
        }

        return prompt;
    }
}
