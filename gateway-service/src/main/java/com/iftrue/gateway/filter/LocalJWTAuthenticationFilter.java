package com.iftrue.gateway.filter;

import com.iftrue.gateway.exception.ErrorCode;
import com.iftrue.gateway.repository.TokenBlacklistRepository;
import com.iftrue.gateway.response.ResponseWriter;
import com.iftrue.gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "JWT")
public class LocalJWTAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    private final ResponseWriter responseWriter;


    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        // 인증이 필요 없는 요청
        if (path.equals("/api/v1/users/login")
                || path.equals("/api/v1/users/signup")
                || path.equals("/api/v1/users/refresh")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);

        if (token == null) {
            return unauthorized(exchange, ErrorCode.INVALID_TOKEN);
        }

        try {
            Claims claims = jwtUtil.parseClaims(token);

            String userId = jwtUtil.getUserId(claims);
            String role = jwtUtil.getRole(claims);
            String hubId = jwtUtil.getHubId(claims);
            String companyId = jwtUtil.getCompanyId(claims);

            if (userId == null || role == null) {
                return unauthorized(exchange, ErrorCode.INVALID_TOKEN);
            }

            // 블랙리스트 검사
            return tokenBlacklistRepository.exists(token)
                    .flatMap(isBlacklisted -> {

                        if (isBlacklisted) {
                            log.warn("Blacklisted JWT access attempt");
                            return unauthorized(exchange, ErrorCode.INVALID_TOKEN);
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
                    }).onErrorResume(e -> {
                        log.error("Redis blacklist check failed", e);
                        return unauthorized(exchange,ErrorCode.SERVICE_UNAVAILABLE);
                    });
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return unauthorized(exchange, ErrorCode.INVALID_TOKEN);
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

    private Mono<Void> unauthorized(ServerWebExchange exchange,ErrorCode errorCode) {

        return responseWriter.write(
                exchange,
                ErrorCode.INVALID_TOKEN
        );
    }
}