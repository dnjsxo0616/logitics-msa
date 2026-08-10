package com.iftrue.hub.domain;

import com.iftrue.hub.global.config.AuditorAwareImpl;
import com.iftrue.hub.global.config.JpaAuditingConfig;
import com.iftrue.hub.global.security.CurrentUserProvider;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfig.class, AuditorAwareImpl.class, CurrentUserProvider.class})
@ActiveProfiles("test")
@DisplayName("[Repository] 허브 이동 경로 DB 제약 테스트")
class HubRouteRepositoryTest {

    @Autowired
    private HubRouteRepository hubRouteRepository;

    @Autowired
    private HubRepository hubRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("경로 저장 후 재조회하면 출발-도착 방향과 값이 그대로 보존된다")
    void routeDirectionAndValuesArePreservedAfterReload() {
        UUID departureHubId = hubRepository.save(hub("방향 출발 허브")).getId();
        UUID arrivalHubId = hubRepository.save(hub("방향 도착 허브")).getId();

        UUID routeId = hubRouteRepository.save(
                HubRoute.create(departureHubId, arrivalHubId, 300, new BigDecimal("325.50"))).getId();

        entityManager.flush();
        entityManager.clear();

        HubRoute found = hubRouteRepository.findById(routeId).orElseThrow();
        assertThat(found.getDepartureHubId()).isEqualTo(departureHubId);
        assertThat(found.getArrivalHubId()).isEqualTo(arrivalHubId);
        assertThat(found.getDurationMinutes()).isEqualTo(300);
        assertThat(found.getDistanceKm()).isEqualByComparingTo("325.50");
    }

    @Test
    @DisplayName("역방향 이동 경로는 별개 경로로 저장된다")
    void reverseDirectionIsStoredSeparately() {
        UUID seoulId = hubRepository.save(hub("역방향 서울 허브")).getId();
        UUID busanId = hubRepository.save(hub("역방향 부산 허브")).getId();
        hubRouteRepository.saveAndFlush(
                HubRoute.create(seoulId, busanId, 300, new BigDecimal("325.50")));

        assertThatCode(() -> hubRouteRepository.saveAndFlush(
                HubRoute.create(busanId, seoulId, 310, new BigDecimal("326.00"))))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("동일한 이동 경로를 중복 저장하면 uq_route_pair 제약 위반이 발생한다")
    void duplicateActiveRoutePairViolatesUniqueConstraint() {
        UUID departureHubId = hubRepository.save(hub("중복 출발 허브")).getId();
        UUID arrivalHubId = hubRepository.save(hub("중복 도착 허브")).getId();
        hubRouteRepository.saveAndFlush(
                HubRoute.create(departureHubId, arrivalHubId, 300, new BigDecimal("325.50")));

        assertThatThrownBy(() -> hubRouteRepository.saveAndFlush(
                HubRoute.create(departureHubId, arrivalHubId, 999, new BigDecimal("1.00"))))
                .isInstanceOf(DataIntegrityViolationException.class)
                .satisfies(exception -> assertThat(
                        extractConstraintName((DataIntegrityViolationException) exception))
                        .isEqualTo("uq_route_pair"));
    }

    @Test
    @DisplayName("기존 경로가 soft delete 상태이면 동일한 출발-도착 경로를 다시 저장할 수 있다")
    void sameRoutePairCanBeCreatedAgainAfterSoftDelete() {
        UUID departureHubId = hubRepository.save(hub("재등록 출발 허브")).getId();
        UUID arrivalHubId = hubRepository.save(hub("재등록 도착 허브")).getId();
        HubRoute deletedRoute = hubRouteRepository.saveAndFlush(
                HubRoute.create(departureHubId, arrivalHubId, 300, new BigDecimal("325.50")));
        deletedRoute.softDelete(UUID.randomUUID());
        hubRouteRepository.saveAndFlush(deletedRoute);

        assertThatCode(() -> hubRouteRepository.saveAndFlush(
                HubRoute.create(departureHubId, arrivalHubId, 300, new BigDecimal("325.50"))))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("soft delete된 이동 경로는 단건 조회에서 제외된다")
    void softDeletedRouteRemainsInDatabaseButIsExcludedFromActiveLookup() {
        UUID departureHubId = hubRepository.save(hub("단건 조회 출발 허브")).getId();
        UUID arrivalHubId = hubRepository.save(hub("단건 조회 도착 허브")).getId();
        HubRoute route = hubRouteRepository.saveAndFlush(
                HubRoute.create(departureHubId, arrivalHubId, 300, new BigDecimal("325.50")));
        UUID routeId = route.getId();
        route.softDelete(UUID.randomUUID());
        hubRouteRepository.saveAndFlush(route);

        entityManager.clear();

        assertThat(hubRouteRepository.findById(routeId)).isPresent();
        assertThat(hubRouteRepository.findByIdAndDeletedAtIsNull(routeId)).isEmpty();
    }

    @Test
    @DisplayName("soft delete된 이동 경로는 목록 조회에서 제외된다")
    void softDeletedRouteIsExcludedFromActiveRouteList() {
        UUID hubA = hubRepository.save(hub("목록 조회 허브1")).getId();
        UUID hubB = hubRepository.save(hub("목록 조회 허브2")).getId();

        UUID activeRouteId = hubRouteRepository.saveAndFlush(
                HubRoute.create(hubA, hubB, 300, new BigDecimal("325.50"))).getId();
        HubRoute deletedRoute = hubRouteRepository.saveAndFlush(
                HubRoute.create(hubB, hubA, 310, new BigDecimal("326.00")));
        UUID deletedRouteId = deletedRoute.getId();
        deletedRoute.softDelete(UUID.randomUUID());
        hubRouteRepository.saveAndFlush(deletedRoute);

        entityManager.clear();

        Page<HubRoute> page = hubRouteRepository.findAllByDeletedAtIsNull(PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(HubRoute::getId)
                .contains(activeRouteId)
                .doesNotContain(deletedRouteId);
    }

    @Test
    @DisplayName("검색 시 출발 허브로 필터링하면 해당 허브에서 출발하는 경로만 반환되고 역방향은 제외된다")
    void searchByDepartureHubReturnsOnlyDepartingRoutes() {
        UUID hubA = hubRepository.save(hub("검색 방향 허브1")).getId();
        UUID hubB = hubRepository.save(hub("검색 방향 허브2")).getId();

        UUID aToB = hubRouteRepository.saveAndFlush(
                HubRoute.create(hubA, hubB, 300, new BigDecimal("325.50"))).getId();
        UUID bToA = hubRouteRepository.saveAndFlush(
                HubRoute.create(hubB, hubA, 310, new BigDecimal("326.00"))).getId();

        entityManager.clear();

        Page<HubRoute> page = hubRouteRepository.search(hubA, null, PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(HubRoute::getId)
                .contains(aToB)
                .doesNotContain(bToA);
    }

    @Test
    @DisplayName("검색 결과에서 soft delete된 이동 경로는 제외된다")
    void searchExcludesSoftDeletedRoutes() {
        UUID hubC = hubRepository.save(hub("검색 제외 허브1")).getId();
        UUID hubD = hubRepository.save(hub("검색 제외 허브2")).getId();

        UUID activeRouteId = hubRouteRepository.saveAndFlush(
                HubRoute.create(hubC, hubD, 300, new BigDecimal("325.50"))).getId();
        HubRoute deletedRoute = hubRouteRepository.saveAndFlush(
                HubRoute.create(hubD, hubC, 310, new BigDecimal("326.00")));
        UUID deletedRouteId = deletedRoute.getId();
        deletedRoute.softDelete(UUID.randomUUID());
        hubRouteRepository.saveAndFlush(deletedRoute);

        entityManager.clear();

        Page<HubRoute> page = hubRouteRepository.search(null, null, PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(HubRoute::getId)
                .contains(activeRouteId)
                .doesNotContain(deletedRouteId);
    }

    private String extractConstraintName(DataIntegrityViolationException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraintException) {
                return constraintException.getConstraintName();
            }
            cause = cause.getCause();
        }
        return null;
    }

    private Hub hub(String name) {
        return Hub.create(name, "테스트 주소",
                new BigDecimal("37.123456"), new BigDecimal("126.654321"));
    }
}
