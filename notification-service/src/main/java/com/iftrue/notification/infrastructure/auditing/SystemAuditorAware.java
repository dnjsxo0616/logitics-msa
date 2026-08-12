package com.iftrue.notification.infrastructure.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("systemAuditorAware")
public class SystemAuditorAware implements AuditorAware<String> {

    private static final String SYSTEM_AUDITOR = "notification-service";

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SYSTEM_AUDITOR);
    }
}
