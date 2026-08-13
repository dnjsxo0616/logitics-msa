package com.iftrue.hub.presentation;

import com.iftrue.hub.domain.Hub;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.domain.HubRoute;
import com.iftrue.hub.domain.HubRouteRepository;
import com.iftrue.hub.global.security.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("[Integration] 허브 내부 API (X-Service-Key 인증)")
class InternalHubApiIntegrationTest {

    private static final String SERVICE_KEY_HEADER = "X-Service-Key";
    private static final String VALID_SERVICE_KEY = "test-internal-key";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HubRepository hubRepository;

    @Autowired
    private HubRouteRepository hubRouteRepository;

    @BeforeEach
    void setUpAuditor() {
        AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "MASTER");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_MASTER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }


    private UUID saveHub() {
        return hubRepository.save(Hub.create(
                "internal-" + UUID.randomUUID(),
                "테스트 주소",
                new BigDecimal("37.5665"),
                new BigDecimal("126.9780"))).getId();
    }

    private void saveRoute(UUID departureHubId, UUID arrivalHubId) {
        hubRouteRepository.save(
                HubRoute.create(departureHubId, arrivalHubId, 300, new BigDecimal("325.50")));
    }


    @Nested
    @DisplayName("서비스 키 인증")
    class ServiceKeyAuth {

        @Test
        @DisplayName("X-Service-Key 헤더가 없을 시 401 H-007을 반환한다")
        void missingServiceKey_returns401() throws Exception {
            UUID hubId = saveHub();

            mockMvc.perform(get("/api/v1/internal/hubs/{hubId}", hubId))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("H-007"));
        }

        @Test
        @DisplayName("X-Service-Key가 틀리면 401 H-007을 반환한다")
        void wrongServiceKey_returns401() throws Exception {
            UUID hubId = saveHub();

            mockMvc.perform(get("/api/v1/internal/hubs/{hubId}", hubId)
                            .header(SERVICE_KEY_HEADER, "wrong-key"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("H-007"));
        }
    }


    @Nested
    @DisplayName("내부 허브 조회")
    class InternalHub {

        @Test
        @DisplayName("유효한 키로 존재하는 허브를 조회하면 200과 raw DTO를 반환한다")
        void getHub_validKey_returns200() throws Exception {
            UUID hubId = saveHub();

            mockMvc.perform(get("/api/v1/internal/hubs/{hubId}", hubId)
                            .header(SERVICE_KEY_HEADER, VALID_SERVICE_KEY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(hubId.toString()))
                    .andExpect(jsonPath("$.name").isNotEmpty())
                    .andExpect(jsonPath("$.latitude").value(37.5665));
        }

        @Test
        @DisplayName("존재하지 않는 허브를 조회하면 404 H-001을 반환한다")
        void getHub_notFound_returns404() throws Exception {
            mockMvc.perform(get("/api/v1/internal/hubs/{hubId}", UUID.randomUUID())
                            .header(SERVICE_KEY_HEADER, VALID_SERVICE_KEY))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("H-001"));
        }

        @Test
        @DisplayName("존재하는 허브의 exists 조회는 200과 exists=true를 반환한다")
        void existsHub_existing_returnsTrue() throws Exception {
            UUID hubId = saveHub();

            mockMvc.perform(get("/api/v1/internal/hubs/{hubId}/exists", hubId)
                            .header(SERVICE_KEY_HEADER, VALID_SERVICE_KEY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hubId").value(hubId.toString()))
                    .andExpect(jsonPath("$.exists").value(true));
        }

        @Test
        @DisplayName("존재하지 않는 허브의 exists 조회는 200과 exists=false를 반환한다(예외 아님)")
        void existsHub_missing_returnsFalse() throws Exception {
            mockMvc.perform(get("/api/v1/internal/hubs/{hubId}/exists", UUID.randomUUID())
                            .header(SERVICE_KEY_HEADER, VALID_SERVICE_KEY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exists").value(false));
        }
    }


    @Nested
    @DisplayName("내부 이동경로 조회 (path)")
    class InternalRoutePath {

        @Test
        @DisplayName("두 허브 사이에 경로가 있으면 200과 단일 구간을 반환한다")
        void path_directRoute_returns200() throws Exception {
            UUID departure = saveHub();
            UUID arrival = saveHub();
            saveRoute(departure, arrival);

            mockMvc.perform(get("/api/v1/internal/hub-routes/path")
                            .param("departureHubId", departure.toString())
                            .param("arrivalHubId", arrival.toString())
                            .header(SERVICE_KEY_HEADER, VALID_SERVICE_KEY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalDuration").value(300))
                    .andExpect(jsonPath("$.segments.length()").value(1))
                    .andExpect(jsonPath("$.segments[0].departureHubId").value(departure.toString()))
                    .andExpect(jsonPath("$.segments[0].arrivalHubId").value(arrival.toString()))
                    .andExpect(jsonPath("$.segments[0].duration").value(300));
        }

        @Test
        @DisplayName("출발지와 도착지가 같으면 200과 sameHub(구간 없음, 소요 0)를 반환한다")
        void path_sameHub_returns200() throws Exception {
            UUID hubId = saveHub();

            mockMvc.perform(get("/api/v1/internal/hub-routes/path")
                            .param("departureHubId", hubId.toString())
                            .param("arrivalHubId", hubId.toString())
                            .header(SERVICE_KEY_HEADER, VALID_SERVICE_KEY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalDuration").value(0))
                    .andExpect(jsonPath("$.segments.length()").value(0));
        }

        @Test
        @DisplayName("허브는 있지만 경로가 없으면 404 H-003을 반환한다")
        void path_noRoute_returns404() throws Exception {
            UUID departure = saveHub();
            UUID arrival = saveHub();

            mockMvc.perform(get("/api/v1/internal/hub-routes/path")
                            .param("departureHubId", departure.toString())
                            .param("arrivalHubId", arrival.toString())
                            .header(SERVICE_KEY_HEADER, VALID_SERVICE_KEY))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("H-003"));
        }

        @Test
        @DisplayName("존재하지 않는 허브를 참조하면 404 H-001을 반환한다")
        void path_missingHub_returns404() throws Exception {
            UUID arrival = saveHub();

            mockMvc.perform(get("/api/v1/internal/hub-routes/path")
                            .param("departureHubId", UUID.randomUUID().toString())
                            .param("arrivalHubId", arrival.toString())
                            .header(SERVICE_KEY_HEADER, VALID_SERVICE_KEY))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("H-001"));
        }

        @Test
        @DisplayName("서비스 키가 없으면 경로 조회도 401 H-007을 반환한다")
        void path_missingServiceKey_returns401() throws Exception {
            UUID departure = saveHub();
            UUID arrival = saveHub();

            mockMvc.perform(get("/api/v1/internal/hub-routes/path")
                            .param("departureHubId", departure.toString())
                            .param("arrivalHubId", arrival.toString()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("H-007"));
        }
    }
}
