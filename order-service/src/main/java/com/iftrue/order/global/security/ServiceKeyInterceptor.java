package com.iftrue.order.global.security;

import com.iftrue.order.global.exception.BusinessException;
import com.iftrue.order.global.exception.OrderErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
@Component
public class ServiceKeyInterceptor implements HandlerInterceptor {

    private static final String SERVICE_KEY_HEADER = "X-Service-Key";

    private final String internalServiceKey;

    public ServiceKeyInterceptor(
            @Value("${internal.service-key}") String internalServiceKey
    ) {
        this.internalServiceKey = internalServiceKey;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String requestKey = request.getHeader(SERVICE_KEY_HEADER);

        if (requestKey == null || !constantTimeEquals(requestKey, internalServiceKey)) {
            log.warn("[Internal-Auth] 내부 서비스 키 검증 실패 uri={}", request.getRequestURI());
            throw new BusinessException(OrderErrorCode.UNAUTHENTICATED_REQUEST);
        }

        return true;
    }

    private boolean constantTimeEquals(String requestKey, String expectedKey) {
        return MessageDigest.isEqual(
                requestKey.getBytes(StandardCharsets.UTF_8),
                expectedKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}
