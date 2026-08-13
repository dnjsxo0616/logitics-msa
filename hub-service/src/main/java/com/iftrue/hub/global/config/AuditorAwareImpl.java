package com.iftrue.hub.global.config;

import com.iftrue.hub.global.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAwareImpl")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<UUID> {

    private final CurrentUserProvider currentUserProvider;

    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.of(currentUserProvider.getCurrentUserId());
    }
}
