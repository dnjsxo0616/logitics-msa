package com.iftrue.user.global.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<UUID> {
    // 시스템 작업용 UUID
    private static final UUID SYSTEM_USER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Override
    public Optional<UUID> getCurrentAuditor() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            return Optional.of(SYSTEM_USER_ID);
        }

        try {
            return Optional.of(UUID.fromString(authentication.getName()));
        } catch (IllegalArgumentException e) {
            return Optional.of(SYSTEM_USER_ID);
        }
    }
}