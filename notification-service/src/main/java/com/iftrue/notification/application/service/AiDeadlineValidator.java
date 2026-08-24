package com.iftrue.notification.application.service;

import com.iftrue.notification.application.dto.GeminiDeadlineResult;
import com.iftrue.notification.application.exception.AiDeadlineValidationException;
import com.iftrue.notification.domain.aialert.AiRequestPayload;
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
    private final AiDeadlineCalculator aiDeadlineCalculator;

    public void validate(
            GeminiDeadlineResult result,
            AiRequestPayload payload
    ) {
        validateResult(result);
        validatePayload(payload);

        Instant finalDeadline = result.finalDeadline().toInstant();
        Instant latestAllowedDeadline =
                aiDeadlineCalculator.calculateLatestDeadline(
                        payload.requestedArrivalAt(),
                        payload.totalExpectedDurationMinutes()
                );

        validateFeasibleDeliveryWindow(
                latestAllowedDeadline,
                payload.deliveryCreatedAt()
        );
        validateNotBeforeDeliveryCreated(
                finalDeadline,
                payload.deliveryCreatedAt()
        );
        validateNotAfterRequestedArrival(
                finalDeadline,
                payload.requestedArrivalAt()
        );
        validateNotAfterLatestAllowedDeadline(
                finalDeadline,
                latestAllowedDeadline
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

    private void validatePayload(AiRequestPayload payload) {
        if (payload == null) {
            throw new AiDeadlineValidationException(
                    "AI 발송 시한 검증에 필요한 요청 정보가 없습니다."
            );
        }

        validateRequiredTime(
                payload.requestedArrivalAt(),
                "희망 도착 시각이 없습니다."
        );
        validateRequiredTime(
                payload.deliveryCreatedAt(),
                "배송 생성 시각이 없습니다."
        );
    }

    private void validateRequiredTime(
            Instant value,
            String message
    ) {
        if (value == null) {
            throw new AiDeadlineValidationException(message);
        }
    }

    private void validateFeasibleDeliveryWindow(
            Instant latestAllowedDeadline,
            Instant deliveryCreatedAt
    ) {
        if (latestAllowedDeadline.isBefore(deliveryCreatedAt)) {
            throw new AiDeadlineValidationException(
                    "총 예상 소요시간을 고려하면 희망 도착 시각을 맞출 수 없습니다."
            );
        }
    }

    private void validateNotBeforeDeliveryCreated(
            Instant finalDeadline,
            Instant deliveryCreatedAt
    ) {
        if (finalDeadline.isBefore(deliveryCreatedAt)) {
            throw new AiDeadlineValidationException(
                    "최종 발송 시한은 배송 생성 시각보다 빠를 수 없습니다."
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

    private void validateNotAfterLatestAllowedDeadline(
            Instant finalDeadline,
            Instant latestAllowedDeadline
    ) {
        if (finalDeadline.isAfter(latestAllowedDeadline)) {
            throw new AiDeadlineValidationException(
                    "최종 발송 시한에 총 예상 소요시간이 충분히 반영되지 않았습니다."
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
