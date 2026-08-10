package com.iftrue.hub.application;

import com.iftrue.hub.domain.Hub;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.domain.HubRoute;
import com.iftrue.hub.domain.HubRouteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("[Integration] 허브 삭제 시 연관 경로 soft delete 테스트")
class HubDeleteCascadeIntegrationTest {

    @Autowired
    private HubService hubService;

    @Autowired
    private HubRepository hubRepository;

    @MockitoSpyBean
    private HubRouteRepository hubRouteRepository;

    @Test
    @DisplayName("허브를 삭제하면 해당 허브가 출발 또는 도착인 활성 경로만 함께 soft delete된다")
    void deletingHubSoftDeletesOnlyRelatedActiveRoutes() {
        UUID targetHubId = hubRepository.save(hub()).getId();
        UUID otherHubId = hubRepository.save(hub()).getId();
        UUID unrelatedHubA = hubRepository.save(hub()).getId();
        UUID unrelatedHubB = hubRepository.save(hub()).getId();

        UUID outgoingRouteId = hubRouteRepository.save(
                HubRoute.create(targetHubId, otherHubId, 300, new BigDecimal("325.50"))).getId();
        UUID incomingRouteId = hubRouteRepository.save(
                HubRoute.create(otherHubId, targetHubId, 310, new BigDecimal("326.00"))).getId();
        UUID unrelatedRouteId = hubRouteRepository.save(
                HubRoute.create(unrelatedHubA, unrelatedHubB, 100, new BigDecimal("50.00"))).getId();

        hubService.deleteHub(targetHubId);

        assertThat(hubRepository.findByIdAndDeletedAtIsNull(targetHubId)).isEmpty();
        assertThat(hubRouteRepository.findByIdAndDeletedAtIsNull(outgoingRouteId)).isEmpty();
        assertThat(hubRouteRepository.findByIdAndDeletedAtIsNull(incomingRouteId)).isEmpty();
        assertThat(hubRouteRepository.findByIdAndDeletedAtIsNull(unrelatedRouteId)).isPresent();
    }

    @Test
    @DisplayName("연관 경로 처리 중 예외가 발생하면 허브 삭제까지 전체 롤백된다")
    void failureDuringRelatedRouteProcessingRollsBackHubDeletion() {
        UUID targetHubId = hubRepository.save(hub()).getId();

        doThrow(new RuntimeException("forced cascade failure"))
                .when(hubRouteRepository).findActiveRoutesByHubId(any(UUID.class));

        assertThatThrownBy(() -> hubService.deleteHub(targetHubId))
                .isInstanceOf(RuntimeException.class);

        assertThat(hubRepository.findByIdAndDeletedAtIsNull(targetHubId)).isPresent();
    }

    private Hub hub() {
        return Hub.create("cascade-" + UUID.randomUUID(), "테스트 주소",
                new BigDecimal("37.123456"), new BigDecimal("126.654321"));
    }
}
