package com.iftrue.hub.global.config;

import com.iftrue.hub.global.security.ServiceKeyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ServiceKeyInterceptor serviceKeyInterceptor;

    public WebConfig(ServiceKeyInterceptor serviceKeyInterceptor) {
        this.serviceKeyInterceptor = serviceKeyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serviceKeyInterceptor)
                .addPathPatterns("/api/v1/internal/**");
    }
}
