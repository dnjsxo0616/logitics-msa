package com.iftrue.delivery.global.interceptor;

import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ServiceKeyInterceptor implements HandlerInterceptor {

    private static String SERVICE_KEY_HEADER = "X-Service-Key";

    private final String internalServiceKey;

    public ServiceKeyInterceptor(
            @Value("${internal.service-key}") String internalServiceKey
    ) {
        this.internalServiceKey = internalServiceKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestKey = request.getHeader(SERVICE_KEY_HEADER);

        if(requestKey == null || !requestKey.equals(internalServiceKey)){
            log.warn("[Internal-Auth] 내부 서비스 키 검증 실패 uri={}", request.getRequestURI());
            throw new DeliveryServiceException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        return true;
    }
}
