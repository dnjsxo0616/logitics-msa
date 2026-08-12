package com.iftrue.notification.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iftrue.notification.application.dto.GeminiDeadlineResult;
import com.iftrue.notification.domain.aialert.DeliveryPayload;
import com.iftrue.notification.domain.aialert.OrderPayload;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import com.iftrue.notification.infrastructure.client.ai.GeminiClient;
import com.iftrue.notification.infrastructure.client.ai.dto.GeminiDeadlineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class AiDeadlineGenerationService {

    private final AiDeadlinePromptFactory promptFactory;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public GeminiDeadlineResult generate(OrderPayload order, DeliveryPayload delivery) {
        String prompt = promptFactory.create(order, delivery);
        String aiResponse = geminiClient.generate(prompt);
        Instant finalDeadline = parseFinalDeadline(aiResponse);

        validateDeadline(finalDeadline, order);

        return new GeminiDeadlineResult(prompt, aiResponse, finalDeadline);
    }

    private Instant parseFinalDeadline(String aiResponse) {
        try {
            GeminiDeadlineResponse response = objectMapper.readerFor(GeminiDeadlineResponse.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(aiResponse);

            if (response.finalDeadline() == null || response.finalDeadline().isBlank()) {
                throw new BusinessException(NotificationErrorCode.AI_PROCESSING_FAILED);
            }

            return OffsetDateTime.parse(response.finalDeadline()).toInstant();
        } catch (JsonProcessingException | DateTimeParseException exception) {
            throw new BusinessException(NotificationErrorCode.AI_PROCESSING_FAILED);
        }
    }

    private void validateDeadline(Instant finalDeadline, OrderPayload order) {
        if (finalDeadline.isBefore(order.orderedAt())
                || finalDeadline.isAfter(order.requestedArrivalAt())) {
            throw new BusinessException(NotificationErrorCode.AI_PROCESSING_FAILED);
        }
    }
}
