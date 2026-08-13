package com.iftrue.notification.application.service;

import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiRequestPayload;
import com.iftrue.notification.global.config.AiDeadlineProperties;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
public class SlackMessageFormatter {

    private static final String EMPTY_VALUE = "없음";

    private final DateTimeFormatter dateTimeFormatter;

    public SlackMessageFormatter(AiDeadlineProperties properties) {
        this.dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm")
                .withZone(properties.zoneId());
    }

    public String format(AiAlert aiAlert) {
        AiRequestPayload payload = aiAlert.getRequestPayload();

        return """
                :package: *배송 요청 알림*

                *주문 정보*
                • 주문 번호: %s
                • 주문자: %s (%s)
                • 주문 시간: %s
                • 상품: %s / %d개
                • 요청 사항: %s

                *배송 정보*
                • 발송지: %s
                • 경유지: %s
                • 도착지: %s
                • 배송 담당자: %s
                • 희망 도착 기한: *%s*
                • 최종 발송 시한: *%s*
                """.formatted(
                aiAlert.getOrderId(),
                escape(payload.requesterName()),
                escape(payload.requesterEmail()),
                dateTimeFormatter.format(payload.orderedAt()),
                escape(payload.productName()),
                payload.quantity(),
                valueOrEmpty(payload.requestMessage()),
                escape(payload.departureHub().name()),
                formatTransitHubs(payload),
                escape(payload.destinationAddress()),
                escape(payload.departureHubManager().name()),
                dateTimeFormatter.format(payload.requestedArrivalAt()),
                dateTimeFormatter.format(aiAlert.getFinalDeadline())
        ).stripTrailing();
    }

    private String formatTransitHubs(AiRequestPayload payload) {
        if (payload.transitHubs() == null || payload.transitHubs().isEmpty()) {
            return EMPTY_VALUE;
        }

        return payload.transitHubs().stream()
                .sorted((first, second) -> Integer.compare(
                        first.sequence(),
                        second.sequence()
                ))
                .map(AiRequestPayload.TransitHub::name)
                .map(SlackMessageFormatter::escape)
                .collect(Collectors.joining(" → "));
    }

    private static String valueOrEmpty(String value) {
        if (value == null || value.isBlank()) {
            return EMPTY_VALUE;
        }

        return escape(value);
    }

    private static String escape(String value) {
        if (value == null) {
            return EMPTY_VALUE;
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
