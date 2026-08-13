package com.iftrue.notification.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AiDeadlineProperties.class)
public class AiConfig {

    @Bean
    public ChatClient geminiChatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
