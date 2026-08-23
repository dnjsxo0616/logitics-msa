package com.iftrue.notification.global.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
public class GeminiConfig {

    private static final String API_KEY_HEADER = "x-goog-api-key";

    @Bean
    public RestClient geminiRestClient(GeminiProperties properties) {
        ClientHttpRequestFactorySettings settings =
                ClientHttpRequestFactorySettings.defaults()
                        .withConnectTimeout(properties.connectTimeout())
                        .withReadTimeout(properties.readTimeout());

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .defaultHeader(
                        HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .requestFactory(
                        ClientHttpRequestFactoryBuilder.detect()
                                .build(settings)
                );

        if (StringUtils.hasText(properties.apiKey())) {
            builder.defaultHeader(API_KEY_HEADER, properties.apiKey());
        }

        return builder.build();
    }
}
