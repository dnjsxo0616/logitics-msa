package com.iftrue.hub.application;

import com.iftrue.hub.application.metrics.HubCoordinate;
import com.iftrue.hub.application.metrics.HubRouteMetrics;
import com.iftrue.hub.application.metrics.HubRouteMetricsProvider;
import com.iftrue.hub.domain.Hub;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.domain.HubRoute;
import com.iftrue.hub.domain.HubRouteRepository;
import com.iftrue.hub.global.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubRouteBatchService {

    private final HubRepository hubRepository;
    private final HubRouteRepository hubRouteRepository;
    private final HubRouteMetricsProvider hubRouteMetricsProvider;

    @Value("${hub-route.generate-all.throttle-ms:200}")
    private long throttleMs;

    @CacheEvict(cacheNames = CacheConfig.HUB_ROUTE_PATH, allEntries = true)
    public HubRouteBatchResult refreshAllRoutes() {
        List<Hub> hubs = hubRepository.findAllByDeletedAtIsNull();
        if (hubs.size() < 2) {
            log.info("[HubRoute-batch] 허브가 2개 미만이라 일괄 갱신 생략 count={}", hubs.size());
            return new HubRouteBatchResult(0, 0, 0);
        }

        int created = 0;
        int updated = 0;
        int failed = 0;

        for (Hub departure : hubs) {
            for (Hub arrival : hubs) {
                if (departure.getId().equals(arrival.getId())) {
                    continue;
                }

                try {
                    HubRouteMetrics metrics = hubRouteMetricsProvider.fetch(
                            new HubCoordinate(departure.getLatitude(), departure.getLongitude()),
                            new HubCoordinate(arrival.getLatitude(), arrival.getLongitude())
                    );

                    Optional<HubRoute> existing = hubRouteRepository
                            .findByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(
                                    departure.getId(), arrival.getId());

                    if (existing.isPresent()) {
                        HubRoute route = existing.get();
                        route.update(metrics.durationMinutes(), metrics.distanceKm());
                        hubRouteRepository.save(route);
                        updated++;
                    } else {
                        hubRouteRepository.save(HubRoute.create(
                                departure.getId(), arrival.getId(),
                                metrics.durationMinutes(), metrics.distanceKm()));
                        created++;
                    }

                    throttle();
                } catch (Exception exception) {
                    failed++;
                    log.warn("[HubRoute-batch] 경로 갱신 실패 departureHubId={}, arrivalHubId={}, cause={}",
                            departure.getId(), arrival.getId(), exception.getClass().getSimpleName());
                }
            }
        }

        log.info("[HubRoute-batch] 일괄 갱신 완료 created={}, updated={}, failed={}", created, updated, failed);
        return new HubRouteBatchResult(created, updated, failed);
    }

    private void throttle() {
        if (throttleMs <= 0) {
            return;
        }
        try {
            Thread.sleep(throttleMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
