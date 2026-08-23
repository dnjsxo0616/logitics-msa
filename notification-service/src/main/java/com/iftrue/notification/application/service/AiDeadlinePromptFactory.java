package com.iftrue.notification.application.service;

import com.iftrue.notification.domain.aialert.AiRequestPayload;
import com.iftrue.notification.global.config.NotificationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AiDeadlinePromptFactory {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final NotificationProperties notificationProperties;

    public String create(AiRequestPayload payload) {
        String transitHubDescription = createTransitHubDescription(payload);
        String requestMessage = StringUtils.hasText(payload.requestMessage())
                ? payload.requestMessage()
                : "없음";

        return """
                당신은 물류 배송 계획을 담당하는 AI입니다.

                다음 배송이 희망 도착 시각 안에 완료되도록 최종 발송 시한을 계산하세요.

                [시간 기준]
                - 기준 시간대: %s
                - 업무 시작 시각: %s
                - 업무 종료 시각: %s

                [주문 정보]
                - 주문 시각: %s
                - 배송 생성 시각: %s
                - 상품명: %s
                - 수량: %d
                - 요청사항: %s

                [배송 정보]
                - 출발 허브: %s
                - 출발 허브 주소: %s
                - 경유지:
                %s
                - 도착지: %s
                - 총 예상 소요시간: %d분
                - 희망 도착 시각: %s

                [응답 규칙]
                - 희망 도착 시각보다 늦지 않도록 계산하세요.
                - 총 예상 소요시간과 업무시간을 고려하세요.
                - 반드시 JSON만 반환하세요.
                - finalDeadline은 기준 시간대의 ISO-8601 형식으로 반환하세요.
                - 설명은 짧고 명확하게 작성하세요.

                [응답 형식]
                {
                  "finalDeadline": "2026-08-24T09:00:00+09:00",
                  "reason": "계산 근거"
                }
                """.formatted(
                notificationProperties.businessZone(),
                notificationProperties.workStartTime(),
                notificationProperties.workEndTime(),
                format(payload.orderedAt()),
                format(payload.deliveryCreatedAt()),
                payload.product().name(),
                payload.product().quantity(),
                requestMessage,
                payload.departureHub().name(),
                payload.departureHub().address(),
                transitHubDescription,
                payload.destinationAddress(),
                payload.totalExpectedDurationMinutes(),
                format(payload.requestedArrivalAt())
        );
    }

    private String createTransitHubDescription(AiRequestPayload payload) {
        if (payload.transitHubs().isEmpty()) {
            return "  - 없음";
        }

        return payload.transitHubs().stream()
                .map(hub -> """
                          - 순서: %d
                            허브명: %s
                            주소: %s
                            예상 소요시간: %d분
                        """.formatted(
                        hub.sequence(),
                        hub.name(),
                        hub.address(),
                        hub.expectedDurationMinutes()
                ).stripTrailing())
                .collect(Collectors.joining("\n"));
    }

    private String format(Instant instant) {
        ZoneId businessZone = notificationProperties.businessZone();

        return DATE_TIME_FORMATTER.format(
                instant.atZone(businessZone)
        );
    }
}
