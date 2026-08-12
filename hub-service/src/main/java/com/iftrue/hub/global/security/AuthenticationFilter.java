package com.iftrue.hub.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String ROLE_PREFIX = "ROLE_";

    private static final Set<String> ALLOWED_ROLES =
            Set.of("MASTER", "HUB_MANAGER", "DELIVERY_MANAGER", "SUPPLIER_MANAGER");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader(USER_ID_HEADER);
        String role = request.getHeader(USER_ROLE_HEADER);

        if (isNotBlank(userId) && isNotBlank(role)) {
            try {
                UUID parsedUserId = UUID.fromString(userId);

                if (!ALLOWED_ROLES.contains(role)) {
                    log.warn("[Auth] 잘못된 X-User-Role value={}", role);
                    filterChain.doFilter(request, response);
                    return;
                }

                AuthenticatedUser principal = new AuthenticatedUser(parsedUserId, role);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role)));

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);

            } catch (IllegalArgumentException exception) {
                log.warn("[Auth] 잘못된 X-User-Id 형식 value={}", userId);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
