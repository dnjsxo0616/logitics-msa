package com.iftrue.order.global.config;

import com.iftrue.order.global.security.AuthenticatedUser;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_COMPANY_ID_HEADER = "X-User-Company-Id";

    @Bean
    public RequestInterceptor authenticationHeaderInterceptor() {
        return requestTemplate -> {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
                return;
            }

            requestTemplate.header(
                    USER_ID_HEADER,
                    user.userId().toString()
            );

            requestTemplate.header(
                    USER_ROLE_HEADER,
                    user.role()
            );

            if (user.companyId() != null) {
                requestTemplate.header(
                        USER_COMPANY_ID_HEADER,
                        user.companyId().toString()
                );
            }
        };
    }
}