package com.iftrue.notification.infrastructure.auditing;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String SYSTEM_AUDITOR = "notification-service";

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SYSTEM_AUDITOR);
    }
}
