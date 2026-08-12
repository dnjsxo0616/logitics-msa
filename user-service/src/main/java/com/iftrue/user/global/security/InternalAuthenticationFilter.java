package com.iftrue.user.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class InternalAuthenticationFilter extends OncePerRequestFilter {

    private static final String SERVICE_KEY_HEADER = "X-Service-Key";
    private static final String INTERNAL_API_PREFIX = "/api/v1/internal/";

    @Value("${internal.service-key}")
    private String internalServiceKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith(INTERNAL_API_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String serviceKey = request.getHeader(SERVICE_KEY_HEADER);

        if (serviceKey == null || !serviceKey.equals(internalServiceKey)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid service key"
            );
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "internal-service",
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}