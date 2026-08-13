package com.iftrue.user.infrastructure.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class InternalFeignConfig {

    @Bean
    public RequestInterceptor serviceKeyInterceptor(
            @Value("${internal.service-key}") String serviceKey
    ) {
        return template ->
                template.header("X-Service-Key", serviceKey);
    }

}
