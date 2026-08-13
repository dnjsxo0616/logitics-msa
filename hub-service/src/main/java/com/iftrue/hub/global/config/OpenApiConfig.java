package com.iftrue.hub.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hubServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hub Service API")
                        .description("허브 관리, 허브 간 이동 경로 API 문서")
                        .version("v1"));
    }

    @Bean
    public GroupedOpenApi externalApi() {
        return GroupedOpenApi.builder()
                .group("hub-external")
                .pathsToMatch("/api/v1/hubs/**", "/api/v1/hub-routes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi internalApi() {
        return GroupedOpenApi.builder()
                .group("hub-internal")
                .pathsToMatch("/api/v1/internal/**")
                .build();
    }
}
