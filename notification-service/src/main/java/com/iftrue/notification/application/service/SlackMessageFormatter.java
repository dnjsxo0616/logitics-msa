package com.iftrue.notification.application.service;

import com.iftrue.notification.domain.aialert.AiRequestPayload;
import com.iftrue.notification.global.config.NotificationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SlackMessageFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX");

    private final NotificationProperties notificationProperties;

    public String format(
            UUID orderId,
            AiRequestPayload payload,
            Instant finalDeadline
    ) {
        String requestMessage = StringUtils.hasText(payload.requestMessage())
                ? escape(payload.requestMessage())
                : "없음";

        return """
                *[배송 발송 시한 안내]*

                *주문 정보*
                • 주문 번호: %s
                • 주문자: %s
                • 주문자 이메일: %s
                • 주문자 Slack ID: %s
                • 주문 시각: %s
                • 상품: %s
                • 수량: %d개
                • 요청사항: %s

                *배송 정보*
                • 출발지: %s (%s)
                • 경유지:
                %s
                • 도착지: %s
                • 배송 담당자: %s
                • 총 예상 소요시간: %d분
                • 희망 도착 시각: %s

                *AI 계산 결과*
                • 최종 발송 시한: %s
                """.formatted(
                orderId,
                escape(payload.requesterName()),
                escape(payload.requesterEmail()),
                escape(payload.requesterSlackId()),
                format(payload.orderedAt()),
                escape(payload.product().name()),
                payload.product().quantity(),
                requestMessage,
                escape(payload.departureHub().name()),
                escape(payload.departureHub().address()),
                createTransitHubDescription(payload),
                escape(payload.destinationAddress()),
                escape(payload.departureHubManager().name()),
                payload.totalExpectedDurationMinutes(),
                format(payload.requestedArrivalAt()),
                format(finalDeadline)
        ).stripTrailing();
    }

    private String createTransitHubDescription(AiRequestPayload payload) {
        if (payload.transitHubs().isEmpty()) {
            return "  • 없음";
        }

        return payload.transitHubs().stream()
                .map(hub -> "  • %d. %s (%s, 예상 %d분)".formatted(
                        hub.sequence(),
                        escape(hub.name()),
                        escape(hub.address()),
                        hub.expectedDurationMinutes()
                ))
                .collect(Collectors.joining("\n"));
    }

    private String format(Instant instant) {
        ZoneId businessZone = notificationProperties.businessZone();

        return DATE_TIME_FORMATTER.format(
                instant.atZone(businessZone)
        );
    }

    private String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
