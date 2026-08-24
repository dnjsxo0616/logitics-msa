package com.iftrue.notification.infrastructure.client.gemini;

import com.iftrue.notification.global.config.GeminiProperties;
import com.iftrue.notification.infrastructure.client.gemini.dto.GeminiInteractionRequest;
import com.iftrue.notification.infrastructure.client.gemini.dto.GeminiInteractionResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class GeminiClient {

    private static final String INTERACTIONS_PATH = "/v1beta/interactions";
    private static final String COMPLETED_STATUS = "completed";

    private final RestClient geminiRestClient;
    private final GeminiProperties geminiProperties;

    public GeminiClient(
            @Qualifier("geminiRestClient")
            RestClient geminiRestClient,
            GeminiProperties geminiProperties
    ) {
        this.geminiRestClient = geminiRestClient;
        this.geminiProperties = geminiProperties;
    }

    public String generate(String prompt) {
        validateApiKey();
        validatePrompt(prompt);

        GeminiInteractionRequest request =
                GeminiInteractionRequest.create(
                        geminiProperties.model(),
                        prompt
                );

        GeminiInteractionResponse response = callGemini(request);

        return extractOutput(response);
    }

    private GeminiInteractionResponse callGemini(
            GeminiInteractionRequest request
    ) {
        try {
            return geminiRestClient.post()
                    .uri(INTERACTIONS_PATH)
                    .body(request)
                    .retrieve()
                    .body(GeminiInteractionResponse.class);
        } catch (ResourceAccessException exception) {
            throw new GeminiClientException(
                    "Gemini API 연결 또는 응답 시간이 초과되었습니다.",
                    exception
            );
        } catch (RestClientResponseException exception) {
            throw new GeminiClientException(
                    "Gemini API 호출에 실패했습니다. status="
                            + exception.getStatusCode().value(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new GeminiClientException(
                    "Gemini API 응답 처리에 실패했습니다.",
                    exception
            );
        }
    }

    private String extractOutput(
            GeminiInteractionResponse response
    ) {
        if (response == null) {
            throw new GeminiClientException(
                    "Gemini 응답 본문이 없습니다."
            );
        }

        if (!COMPLETED_STATUS.equals(response.status())) {
            throw new GeminiClientException(
                    "Gemini 작업이 완료되지 않았습니다. status="
                            + response.status()
            );
        }

        return response.findOutputText()
                .orElseThrow(() -> new GeminiClientException(
                        "Gemini 응답 텍스트가 없습니다."
                ));
    }

    private void validateApiKey() {
        if (!StringUtils.hasText(geminiProperties.apiKey())) {
            throw new GeminiClientException(
                    "Gemini API 키가 설정되지 않았습니다."
            );
        }
    }

    private void validatePrompt(String prompt) {
        if (!StringUtils.hasText(prompt)) {
            throw new GeminiClientException(
                    "Gemini 프롬프트가 비어 있습니다."
            );
        }
    }
}
