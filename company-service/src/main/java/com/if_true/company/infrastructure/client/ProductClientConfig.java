package com.if_true.company.infrastructure.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ProductClientConfig {

	@Bean
	RequestInterceptor productServiceKeyInterceptor(
		@Value("${internal.service-key}") String serviceKey
	) {
		return template -> template.header("X-Service-Key", serviceKey);
	}
}
