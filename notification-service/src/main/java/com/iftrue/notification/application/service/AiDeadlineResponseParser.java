package com.iftrue.notification.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iftrue.notification.application.dto.GeminiDeadlineResult;
import com.iftrue.notification.application.exception.AiDeadlineResponseParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AiDeadlineResponseParser {

    private final ObjectMapper objectMapper;

    public GeminiDeadlineResult parse(String aiResponse) {
        if (!StringUtils.hasText(aiResponse)) {
            throw new AiDeadlineResponseParseException(
                    "Gemini 응답이 비어 있습니다."
            );
        }

        try {
            return objectMapper.readValue(
                    aiResponse,
                    GeminiDeadlineResult.class
            );
        } catch (JsonProcessingException exception) {
            throw new AiDeadlineResponseParseException(
                    "Gemini 응답 JSON 파싱에 실패했습니다.",
                    exception
            );
        }
    }
}
