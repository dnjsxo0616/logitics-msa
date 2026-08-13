package com.iftrue.hub.infrastructure.tmap;

import com.iftrue.hub.application.metrics.HubCoordinate;
import com.iftrue.hub.application.metrics.HubRouteMetrics;
import com.iftrue.hub.application.metrics.HubRouteMetricsProvider;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
public class TMapHubRouteMetricsProvider implements HubRouteMetricsProvider {

    private static final String ROUTES_PATH = "/tmap/routes?version=1&format=json";
    private static final int MAX_ATTEMPTS = 3;
    private static final long BACKOFF_BASE_MS = 200L;

    private final RestClient tmapRestClient;
    private final String appKey;

    public TMapHubRouteMetricsProvider(
            RestClient tmapRestClient,
            @Value("${tmap.app-key}") String appKey
    ) {
        this.tmapRestClient = tmapRestClient;
        this.appKey = appKey;
    }

    @Override
    public HubRouteMetrics fetch(HubCoordinate departure, HubCoordinate arrival) {
        try {
            TMapRouteResponse response = requestWithRetry(departure, arrival);
            return toMetrics(response);
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("[HubRoute-tmap] 예상하지 못한 TMap 연동 오류 cause={}", exception.getClass().getSimpleName());
            throw new BusinessException(ErrorCode.EXTERNAL_ROUTE_API_ERROR);
        }
    }

    private TMapRouteResponse requestWithRetry(HubCoordinate departure, HubCoordinate arrival) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return callTMap(departure, arrival);

            } catch (HttpClientErrorException exception) {
                handleClientError(exception);

            } catch (ResourceAccessException | HttpServerErrorException e) {
                if (attempt == MAX_ATTEMPTS) {
                    log.warn("[HubRoute-tmap] TMap 호출 재시도 attempts={}, cause={}",
                            attempt, e.getClass().getSimpleName());
                    throw new BusinessException(ErrorCode.EXTERNAL_ROUTE_API_ERROR);
                }
                log.warn("[HubRoute-tmap] TMap 호출 실패, 재시도 attempt={}/{}, cause={}",
                        attempt, MAX_ATTEMPTS, e.getClass().getSimpleName());
                sleepBackoff(attempt);
            }
        }
        throw new BusinessException(ErrorCode.EXTERNAL_ROUTE_API_ERROR);
    }

    private TMapRouteResponse callTMap(HubCoordinate departure, HubCoordinate arrival) {
        TMapRouteRequest body = new TMapRouteRequest(
                departure.longitude(), departure.latitude(),
                arrival.longitude(), arrival.latitude(),
                "WGS84GEO", "WGS84GEO"
        );

        return tmapRestClient.post()
                .uri(ROUTES_PATH)
                .header("appKey", appKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(TMapRouteResponse.class);
    }

    private void handleClientError(HttpClientErrorException e) {
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
            log.error("[HubRoute-tmap] TMap 인증 오류 status={} (appKey 설정 확인 필요)", e.getStatusCode());
        } else {
            log.warn("[HubRoute-tmap] TMap 요청 오류 status={}", e.getStatusCode());
        }
        throw new BusinessException(ErrorCode.EXTERNAL_ROUTE_API_ERROR);
    }

    private HubRouteMetrics toMetrics(TMapRouteResponse response) {
        List<TMapRouteResponse.Feature> features = (response == null) ? null : response.features();
        if (features == null || features.isEmpty()) {
            throwExternalApiError("TMap 응답에 경로 정보가 없습니다.");
        }

        TMapRouteResponse.Feature feature = features.get(0);
        if (feature == null || feature.properties() == null) {
            throwExternalApiError("TMap 응답에 properties가 없습니다.");
        }

        TMapRouteResponse.Properties props = feature.properties();
        if (props.totalDistance() == null || props.totalTime() == null) {
            throwExternalApiError("TMap 응답에 거리 또는 시간이 없습니다.");
        }
        if (props.totalDistance() < 0 || props.totalTime() < 0) {
            throwExternalApiError("TMap 응답의 거리 또는 시간이 유효하지 않습니다.");
        }

        int durationMinutes = (int) Math.ceil(props.totalTime() / 60.0);
        BigDecimal distanceKm = BigDecimal.valueOf(props.totalDistance())
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

        return new HubRouteMetrics(durationMinutes, distanceKm);
    }

    private void throwExternalApiError(String reason) {
        log.warn("[HubRoute-tmap] {}", reason);
        throw new BusinessException(ErrorCode.EXTERNAL_ROUTE_API_ERROR);
    }

    private void sleepBackoff(int attempt) {
        try {
            Thread.sleep(BACKOFF_BASE_MS * attempt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.EXTERNAL_ROUTE_API_ERROR);
        }
    }
}
