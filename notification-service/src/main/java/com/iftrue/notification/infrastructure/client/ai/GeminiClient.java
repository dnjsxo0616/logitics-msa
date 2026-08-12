package com.iftrue.notification.infrastructure.client.ai;

import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final ChatClient geminiChatClient;

    public String generate(String prompt) {
        try {
            String response = geminiChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (!StringUtils.hasText(response)) {
                throw new BusinessException(NotificationErrorCode.AI_PROCESSING_FAILED);
            }

            return response;
        } catch (BusinessException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new BusinessException(NotificationErrorCode.AI_PROCESSING_FAILED);
        }
    }
}
