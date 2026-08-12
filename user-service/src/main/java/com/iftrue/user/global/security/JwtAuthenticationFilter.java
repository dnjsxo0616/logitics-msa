package com.iftrue.user.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String INTERNAL_API_PREFIX = "/api/v1/internal/";

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getRequestURI().startsWith(INTERNAL_API_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userIdHeader = request.getHeader(USER_ID_HEADER);
        String roleHeader = request.getHeader(USER_ROLE_HEADER);

        // Gateway에서 인증 정보가 전달되지 않은 경우
        if (userIdHeader == null || roleHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UUID userId = UUID.fromString(userIdHeader);

            String role = roleHeader.startsWith("ROLE_")
                    ? roleHeader
                    : "ROLE_" + roleHeader;

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (IllegalArgumentException e) {
            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid authentication headers"
            );
        }
    }
}



