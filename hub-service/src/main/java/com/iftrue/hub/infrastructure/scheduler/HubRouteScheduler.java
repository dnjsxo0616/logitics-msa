package com.iftrue.hub.infrastructure.scheduler;

import com.iftrue.hub.application.HubRouteBatchService;
import com.iftrue.hub.global.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "hub-route.generate-all", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HubRouteScheduler {

    private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String SYSTEM_ROLE = "MASTER";

    private final HubRouteBatchService hubRouteBatchService;

    @Scheduled(cron = "${hub-route.generate-all.cron:0 0 4 * * *}", zone = "Asia/Seoul")
    public void refreshAllRoutes() {
        log.info("[HubRoute-scheduler] 허브 경로 일괄 갱신 시작");
        runAsSystem(hubRouteBatchService::refreshAllRoutes);
        log.info("[HubRoute-scheduler] 허브 경로 일괄 갱신 종료");
    }

    private void runAsSystem(Runnable task) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                new AuthenticatedUser(SYSTEM_USER_ID, SYSTEM_ROLE),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + SYSTEM_ROLE))
        ));
        SecurityContextHolder.setContext(context);
        try {
            task.run();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
