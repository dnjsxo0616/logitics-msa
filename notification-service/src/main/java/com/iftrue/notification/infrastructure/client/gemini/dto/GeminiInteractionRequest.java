package com.iftrue.notification.infrastructure.client.gemini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record GeminiInteractionRequest(
        String model,
        String input,

        @JsonProperty("response_format")
        ResponseFormat responseFormat,

        boolean store
) {

    public static GeminiInteractionRequest create(
            String model,
            String prompt
    ) {
        Map<String, SchemaProperty> properties = Map.of(
                "finalDeadline",
                new SchemaProperty(
                        "string",
                        "ISO-8601 형식의 최종 발송 시한"
                ),
                "reason",
                new SchemaProperty(
                        "string",
                        "최종 발송 시한 계산 근거"
                )
        );

        JsonSchema schema = new JsonSchema(
                "object",
                properties,
                List.of("finalDeadline", "reason"),
                false
        );

        ResponseFormat responseFormat = new ResponseFormat(
                "text",
                "application/json",
                schema
        );

        return new GeminiInteractionRequest(
                model,
                prompt,
                responseFormat,
                false
        );
    }

    public record ResponseFormat(
            String type,

            @JsonProperty("mime_type")
            String mimeType,

            JsonSchema schema
    ) {
    }

    public record JsonSchema(
            String type,
            Map<String, SchemaProperty> properties,
            List<String> required,
            boolean additionalProperties
    ) {
    }

    public record SchemaProperty(
            String type,
            String description
    ) {
    }
}
