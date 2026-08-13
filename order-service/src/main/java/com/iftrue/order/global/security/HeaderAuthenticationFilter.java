package com.iftrue.order.global.security;

import com.iftrue.order.global.security.handler.RestAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    //Gateway 헤더 이름 구현시 맞춰서 수정
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_COMPANY_ID_HEADER = "X-User-Company-Id";
    private static final String ROLE_PREFIX = "ROLE_";

    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userIdHeader = request.getHeader(USER_ID_HEADER);

        String roleHeader = request.getHeader(USER_ROLE_HEADER);

        String companyIdHeader = request.getHeader(USER_COMPANY_ID_HEADER);

        if (isBlank(userIdHeader) && isBlank(roleHeader) && isBlank(companyIdHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isBlank(userIdHeader) || isBlank(roleHeader)) {

            handleAuthenticationFailure(
                    request,
                    response,
                    new BadCredentialsException(
                            "필수 인증 헤더가 누락되었습니다."
                    )
            );
            return;
        }

        try {
            UUID userId = UUID.fromString(userIdHeader);
            UUID companyId = parseNullableUuid(companyIdHeader);
            String role = normalizeRole(roleHeader);

            AuthenticatedUser user = new AuthenticatedUser(userId, role, companyId);

            GrantedAuthority authority =
                    new SimpleGrantedAuthority(ROLE_PREFIX + role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user,null, List.of(authority));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

        } catch (IllegalArgumentException exception) {
            handleAuthenticationFailure(
                    request,
                    response,
                    new BadCredentialsException("인증 헤더 형식이 올바르지 않습니다.", exception));
            return;
        }

        filterChain.doFilter(request, response);

    }

    private void handleAuthenticationFailure(HttpServletRequest request,
                                             HttpServletResponse response,
                                             BadCredentialsException exception) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        authenticationEntryPoint.commence(request, response, exception);
    }

    // 일단 MASTER는 소속 업체가 없다고 생각하고 헤더를 생략 가능하다고 구현해둠.
    private UUID parseNullableUuid(String companyIdHeader) {
        if (isBlank(companyIdHeader)) {
            return null;
        }

        return UUID.fromString(companyIdHeader);
    }

    // SUPPLER_MANAGER 형식으로 전달인데 gateway전달 형식보고 추후에 변경
    private String normalizeRole(String roleHeader) {
        return roleHeader.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
