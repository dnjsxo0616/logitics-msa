package com.iftrue.gateway.filter;

import com.iftrue.gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "JWT")
public class LocalJWTAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        if (path.equals("/api/v1/users/login")
                || path.equals("/api/v1/users/signup")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);

        if (token == null) {
            return unauthorized(exchange);
        }

        try {
            Claims claims = jwtUtil.parseClaims(token);

            String userId = jwtUtil.getUserId(claims);
            String role = jwtUtil.getRole(claims);
            String hubId = jwtUtil.getHubId(claims);
            String companyId = jwtUtil.getCompanyId(claims);

            if (userId == null || role == null) {
                return unauthorized(exchange);
            }

            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .headers(headers -> {

                        headers.remove("X-User-Id");
                        headers.remove("X-User-Role");
                        headers.remove("X-User-Hub-Id");
                        headers.remove("X-User-Company-Id");

                        headers.set("X-User-Id", userId);
                        headers.set("X-User-Role", role);

                        if (hubId != null) {
                            headers.set("X-User-Hub-Id", hubId);
                        }

                        if (companyId != null) {
                            headers.set("X-User-Company-Id", companyId);
                        }
                    })
                    .build();

            return chain.filter(
                    exchange.mutate()
                            .request(request)
                            .build()
            );

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return unauthorized(exchange);
        }
    }

    private String extractToken(ServerWebExchange exchange) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader != null
                && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }
}