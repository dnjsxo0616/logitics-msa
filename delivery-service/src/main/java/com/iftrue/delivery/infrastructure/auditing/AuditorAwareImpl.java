package com.iftrue.delivery.infrastructure.auditing;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    private static final String SYSTEM_AUDITOR = "delivery-service";

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SYSTEM_AUDITOR); // TODO: 추후 Security 구현후 로직 분기 처리
    }
}
