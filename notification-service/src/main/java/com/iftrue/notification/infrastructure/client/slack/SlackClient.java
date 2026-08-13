package com.iftrue.notification.infrastructure.client.slack;

import com.iftrue.notification.global.config.SlackProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class SlackClient {

    private final RestClient restClient;
    private final SlackProperties properties;

    public SlackClient(
            @Qualifier("slackRestClient") RestClient restClient,
            SlackProperties properties
    ) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public void sendMessage(String receiverId, String message) {
        try {
            SlackPostMessageResponse response = restClient.post()
                    .uri("/api/chat.postMessage")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + properties.botToken()
                    )
                    .body(new SlackPostMessageRequest(receiverId, message))
                    .retrieve()
                    .body(SlackPostMessageResponse.class);

            if (response == null) {
                throw new SlackApiException("Slack API 응답이 비어 있습니다.");
            }

            if (!response.ok()) {
                String error = StringUtils.hasText(response.error())
                        ? response.error()
                        : "unknown_error";
                throw new SlackApiException("Slack API 오류: " + error);
            }
        } catch (SlackApiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new SlackApiException("Slack API 호출에 실패했습니다.", exception);
        }
    }

    private record SlackPostMessageRequest(
            String channel,
            String text
    ) {
    }

    private record SlackPostMessageResponse(
            boolean ok,
            String error
    ) {
    }
}
