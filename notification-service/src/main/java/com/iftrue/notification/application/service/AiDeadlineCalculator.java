package com.iftrue.notification.application.service;

import com.iftrue.notification.application.exception.AiDeadlineValidationException;
import com.iftrue.notification.global.config.NotificationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class AiDeadlineCalculator {

    private final NotificationProperties notificationProperties;

    public Instant calculateLatestDeadline(
            Instant requestedArrivalAt,
            int totalExpectedDurationMinutes
    ) {
        validateInput(
                requestedArrivalAt,
                totalExpectedDurationMinutes
        );

        ZoneId businessZone = notificationProperties.businessZone();
        LocalTime workStartTime =
                notificationProperties.workStartTime();
        LocalTime workEndTime =
                notificationProperties.workEndTime();

        validateBusinessHours(workStartTime, workEndTime);

        ZonedDateTime cursor = moveIntoBusinessHours(
                requestedArrivalAt.atZone(businessZone),
                workStartTime,
                workEndTime,
                businessZone
        );
        Duration remaining = Duration.ofMinutes(
                totalExpectedDurationMinutes
        );

        while (!remaining.isZero()) {
            ZonedDateTime workStart = ZonedDateTime.of(
                    cursor.toLocalDate(),
                    workStartTime,
                    businessZone
            );
            Duration availableToday = Duration.between(
                    workStart,
                    cursor
            );

            if (remaining.compareTo(availableToday) <= 0) {
                return cursor.minus(remaining).toInstant();
            }

            remaining = remaining.minus(availableToday);
            cursor = previousBusinessDayEnd(
                    cursor.toLocalDate(),
                    workEndTime,
                    businessZone
            );
        }

        return cursor.toInstant();
    }

    private ZonedDateTime moveIntoBusinessHours(
            ZonedDateTime requestedArrivalAt,
            LocalTime workStartTime,
            LocalTime workEndTime,
            ZoneId businessZone
    ) {
        LocalTime requestedTime = requestedArrivalAt.toLocalTime();

        if (requestedTime.isAfter(workEndTime)) {
            return ZonedDateTime.of(
                    requestedArrivalAt.toLocalDate(),
                    workEndTime,
                    businessZone
            );
        }

        if (requestedTime.isBefore(workStartTime)) {
            return previousBusinessDayEnd(
                    requestedArrivalAt.toLocalDate(),
                    workEndTime,
                    businessZone
            );
        }

        return requestedArrivalAt;
    }

    private ZonedDateTime previousBusinessDayEnd(
            LocalDate currentDate,
            LocalTime workEndTime,
            ZoneId businessZone
    ) {
        return ZonedDateTime.of(
                currentDate.minusDays(1),
                workEndTime,
                businessZone
        );
    }

    private void validateInput(
            Instant requestedArrivalAt,
            int totalExpectedDurationMinutes
    ) {
        if (requestedArrivalAt == null) {
            throw new AiDeadlineValidationException(
                    "희망 도착 시각이 없습니다."
            );
        }

        if (totalExpectedDurationMinutes < 0) {
            throw new AiDeadlineValidationException(
                    "총 예상 소요시간은 0분 이상이어야 합니다."
            );
        }
    }

    private void validateBusinessHours(
            LocalTime workStartTime,
            LocalTime workEndTime
    ) {
        if (!workStartTime.isBefore(workEndTime)) {
            throw new AiDeadlineValidationException(
                    "업무 시작 시각은 업무 종료 시각보다 빨라야 합니다."
            );
        }
    }
}
