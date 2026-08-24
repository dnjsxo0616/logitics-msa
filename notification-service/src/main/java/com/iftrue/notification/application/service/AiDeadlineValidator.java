package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.GeminiDeadlineResult;
import com.iftrue.notification.application.exception.AiDeadlineValidationException;
import com.iftrue.notification.global.config.NotificationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class AiDeadlineValidator {

    private final NotificationProperties notificationProperties;

    public void validate(
            GeminiDeadlineResult result,
            Instant requestedArrivalAt
    ) {
        validateResult(result);
        validateRequestedArrivalAt(requestedArrivalAt);

        Instant finalDeadline = result.finalDeadline().toInstant();

        validateNotAfterRequestedArrival(
                finalDeadline,
                requestedArrivalAt
        );
        validateBusinessHours(finalDeadline);
    }

    private void validateResult(GeminiDeadlineResult result) {
        if (result == null) {
            throw new AiDeadlineValidationException(
                    "Gemini 발송 시한 결과가 없습니다."
            );
        }

        if (result.finalDeadline() == null) {
            throw new AiDeadlineValidationException(
                    "Gemini 최종 발송 시한이 없습니다."
            );
        }

        if (!StringUtils.hasText(result.reason())) {
            throw new AiDeadlineValidationException(
                    "Gemini 발송 시한 계산 근거가 없습니다."
            );
        }
    }

    private void validateRequestedArrivalAt(
            Instant requestedArrivalAt
    ) {
        if (requestedArrivalAt == null) {
            throw new AiDeadlineValidationException(
                    "희망 도착 시각이 없습니다."
            );
        }
    }

    private void validateNotAfterRequestedArrival(
            Instant finalDeadline,
            Instant requestedArrivalAt
    ) {
        if (finalDeadline.isAfter(requestedArrivalAt)) {
            throw new AiDeadlineValidationException(
                    "최종 발송 시한은 희망 도착 시각보다 늦을 수 없습니다."
            );
        }
    }

    private void validateBusinessHours(Instant finalDeadline) {
        ZonedDateTime businessDeadline = finalDeadline.atZone(
                notificationProperties.businessZone()
        );
        LocalTime deadlineTime = businessDeadline.toLocalTime();
        LocalTime workStartTime =
                notificationProperties.workStartTime();
        LocalTime workEndTime =
                notificationProperties.workEndTime();

        boolean beforeWorkStart = deadlineTime.isBefore(workStartTime);
        boolean afterWorkEnd = deadlineTime.isAfter(workEndTime);

        if (beforeWorkStart || afterWorkEnd) {
            throw new AiDeadlineValidationException(
                    "최종 발송 시한이 업무시간 범위를 벗어났습니다."
            );
        }
    }
}
