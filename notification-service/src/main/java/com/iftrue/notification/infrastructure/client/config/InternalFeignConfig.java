package com.iftrue.notification.infrastructure.client.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class InternalFeignConfig {

    private static final String SERVICE_KEY_HEADER = "X-Service-Key";

    @Bean
    public RequestInterceptor serviceKeyRequestInterceptor(
            @Value("${internal.service-key}") String serviceKey
    ) {
        return requestTemplate -> requestTemplate.header(SERVICE_KEY_HEADER, serviceKey);
    }
}
