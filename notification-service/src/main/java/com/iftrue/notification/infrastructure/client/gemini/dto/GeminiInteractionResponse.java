package com.iftrue.notification.infrastructure.client.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiInteractionResponse(
        String id,
        String status,
        List<Step> steps
) {

    private static final String MODEL_OUTPUT_TYPE = "model_output";
    private static final String TEXT_CONTENT_TYPE = "text";

    public Optional<String> findOutputText() {
        if (steps == null) {
            return Optional.empty();
        }

        return steps.stream()
                .filter(Objects::nonNull)
                .filter(step -> MODEL_OUTPUT_TYPE.equals(step.type()))
                .filter(step -> step.content() != null)
                .flatMap(step -> step.content().stream())
                .filter(Objects::nonNull)
                .filter(content ->
                        TEXT_CONTENT_TYPE.equals(content.type())
                )
                .map(Content::text)
                .filter(StringUtils::hasText)
                .reduce(String::concat);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Step(
            String type,
            String status,
            List<Content> content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(
            String type,
            String text
    ) {
    }
}
