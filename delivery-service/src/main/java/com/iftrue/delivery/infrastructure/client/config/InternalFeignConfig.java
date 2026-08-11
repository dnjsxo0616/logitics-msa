package com.iftrue.delivery.infrastructure.client.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class InternalFeignConfig {

    @Bean
    public RequestInterceptor serviceKeyInterceptor(
            @Value("${internal.service-key}") String serviceKey
    ) {
        return template ->
                template.header("X-Service-Key", serviceKey);
    }
}
