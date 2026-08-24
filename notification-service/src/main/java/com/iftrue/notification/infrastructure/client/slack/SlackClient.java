package com.iftrue.notification.infrastructure.client.slack;

import com.iftrue.notification.global.config.SlackProperties;
import com.iftrue.notification.infrastructure.client.slack.dto.SlackPostMessageRequest;
import com.iftrue.notification.infrastructure.client.slack.dto.SlackPostMessageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class SlackClient {

    private static final String POST_MESSAGE_PATH =
            "/api/chat.postMessage";

    private final RestClient slackRestClient;
    private final SlackProperties slackProperties;

    public SlackClient(
            @Qualifier("slackRestClient")
            RestClient slackRestClient,
            SlackProperties slackProperties
    ) {
        this.slackRestClient = slackRestClient;
        this.slackProperties = slackProperties;
    }

    public void send(
            String receiverId,
            String message
    ) {
        validateBotToken();
        validateText(receiverId, "Slack 수신자 ID가 없습니다.");
        validateText(message, "Slack 메시지가 없습니다.");

        SlackPostMessageRequest request =
                new SlackPostMessageRequest(receiverId, message);
        SlackPostMessageResponse response = postMessage(request);

        validateResponse(response);
    }

    private SlackPostMessageResponse postMessage(
            SlackPostMessageRequest request
    ) {
        try {
            return slackRestClient.post()
                    .uri(POST_MESSAGE_PATH)
                    .body(request)
                    .retrieve()
                    .body(SlackPostMessageResponse.class);
        } catch (ResourceAccessException exception) {
            throw new SlackClientException(
                    "Slack API 연결 또는 응답 시간이 초과되었습니다.",
                    exception
            );
        } catch (RestClientResponseException exception) {
            throw new SlackClientException(
                    "Slack API 호출에 실패했습니다. status="
                            + exception.getStatusCode().value(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new SlackClientException(
                    "Slack API 응답 처리에 실패했습니다.",
                    exception
            );
        }
    }

    private void validateResponse(SlackPostMessageResponse response) {
        if (response == null) {
            throw new SlackClientException(
                    "Slack 응답 본문이 없습니다."
            );
        }

        if (!response.ok()) {
            String error = StringUtils.hasText(response.error())
                    ? response.error()
                    : "unknown";

            throw new SlackClientException(
                    "Slack 메시지 발송에 실패했습니다. error=" + error
            );
        }
    }

    private void validateBotToken() {
        if (!StringUtils.hasText(slackProperties.botToken())) {
            throw new SlackClientException(
                    "Slack Bot Token이 설정되지 않았습니다."
            );
        }
    }

    private void validateText(
            String value,
            String message
    ) {
        if (!StringUtils.hasText(value)) {
            throw new SlackClientException(message);
        }
    }
}
