package com.iftrue.hub.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("[Integration] 허브 외부 API 테스트")
class HubApiIntegrationTest {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String MASTER_ID = "00000000-0000-0000-0000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private MockHttpServletRequestBuilder asRole(MockHttpServletRequestBuilder builder, String role) {
        return builder.header(HEADER_USER_ID, MASTER_ID).header(HEADER_USER_ROLE, role);
    }

    private String hubBody(String name) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("address", "서울특별시 중구 세종대로 110");
        body.put("latitude", new BigDecimal("37.5665"));
        body.put("longitude", new BigDecimal("126.9780"));
        return objectMapper.writeValueAsString(body);
    }

    private String routeBody(String departureHubId, String arrivalHubId) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("departureHubId", departureHubId);
        body.put("arrivalHubId", arrivalHubId);
        body.put("durationMinutes", 300);
        body.put("distanceKm", new BigDecimal("325.50"));
        return objectMapper.writeValueAsString(body);
    }

    private UUID createHubAsMaster(String name) throws Exception {
        String response = mockMvc.perform(asRole(post("/api/v1/hubs"), "MASTER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hubBody(name)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return UUID.fromString(objectMapper.readTree(response).path("data").path("id").asText());
    }

    private String uniqueName() {
        return "통합테스트-허브-" + UUID.randomUUID();
    }


    @Nested
    @DisplayName("허브 생성: 인증/인가")
    class CreateHubAuth {

        @Test
        @DisplayName("MASTER 권한으로 허브 생성 시 201과 생성된 허브를 반환한다")
        void createAsMaster_returns201() throws Exception {
            String name = uniqueName();

            mockMvc.perform(asRole(post("/api/v1/hubs"), "MASTER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(hubBody(name)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.code").value("success"))
                    .andExpect(jsonPath("$.data.id").isNotEmpty())
                    .andExpect(jsonPath("$.data.name").value(name))
                    .andExpect(jsonPath("$.data.latitude").value(37.5665));
        }

        @Test
        @DisplayName("인증 헤더가 없으면 401 H-007을 반환한다")
        void createWithoutAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/hubs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(hubBody(uniqueName())))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("H-007"));
        }

        @Test
        @DisplayName("MASTER가 아니면(HUB_MANAGER) 생성 시 403 H-008을 반환한다")
        void createAsNonMaster_returns403() throws Exception {
            mockMvc.perform(asRole(post("/api/v1/hubs"), "HUB_MANAGER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(hubBody(uniqueName())))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("H-008"));
        }
    }


    @Nested
    @DisplayName("허브 생성: 검증/중복")
    class CreateHubValidation {

        @Test
        @DisplayName("이름이 공백일 시 400 H-006을 반환한다")
        void blankName_returns400() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of(
                    "name", "   ",
                    "address", "서울특별시 중구 세종대로 110",
                    "latitude", new BigDecimal("37.5665"),
                    "longitude", new BigDecimal("126.9780")));

            mockMvc.perform(asRole(post("/api/v1/hubs"), "MASTER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("H-006"));
        }

        @Test
        @DisplayName("같은 이름으로 두 번 생성 시 두 번째는 409 H-002를 반환한다")
        void duplicateName_returns409() throws Exception {
            String name = uniqueName();
            createHubAsMaster(name);

            mockMvc.perform(asRole(post("/api/v1/hubs"), "MASTER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(hubBody(name)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("H-002"));
        }
    }


    @Nested
    @DisplayName("허브 조회")
    class ReadHub {

        @Test
        @DisplayName("생성한 허브를 id로 단건 조회하면 200과 동일 데이터를 반환한다")
        void getById_returns200() throws Exception {
            String name = uniqueName();
            UUID id = createHubAsMaster(name);

            mockMvc.perform(asRole(get("/api/v1/hubs/{hubId}", id), "MASTER"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("success"))
                    .andExpect(jsonPath("$.data.id").value(id.toString()))
                    .andExpect(jsonPath("$.data.name").value(name));
        }

        @Test
        @DisplayName("존재하지 않는 허브 id면 404 H-001을 반환한다")
        void getById_notFound_returns404() throws Exception {
            mockMvc.perform(asRole(get("/api/v1/hubs/{hubId}", UUID.randomUUID()), "MASTER"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("H-001"));
        }

        @Test
        @DisplayName("로그인 사용자면 목록 조회는 200과 content 배열을 반환한다")
        void getList_returns200() throws Exception {
            mockMvc.perform(asRole(get("/api/v1/hubs?page=0&size=30"), "HUB_MANAGER"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("success"))
                    .andExpect(jsonPath("$.data.content").isArray());
        }
    }


    @Nested
    @DisplayName("허브 이동경로 생성")
    class CreateRoute {

        @Test
        @DisplayName("MASTER가 서로 다른 두 허브로 경로를 만들면 201을 반환한다")
        void createRoute_returns201() throws Exception {
            UUID departure = createHubAsMaster(uniqueName());
            UUID arrival = createHubAsMaster(uniqueName());

            mockMvc.perform(asRole(post("/api/v1/hub-routes"), "MASTER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(routeBody(departure.toString(), arrival.toString())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.data.departureHubId").value(departure.toString()))
                    .andExpect(jsonPath("$.data.arrivalHubId").value(arrival.toString()))
                    .andExpect(jsonPath("$.data.durationMinutes").value(300));
        }

        @Test
        @DisplayName("출발지와 도착지가 같으면 400 H-005를 반환한다")
        void sameEndpoint_returns400() throws Exception {
            UUID hub = createHubAsMaster(uniqueName());

            mockMvc.perform(asRole(post("/api/v1/hub-routes"), "MASTER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(routeBody(hub.toString(), hub.toString())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("H-005"));
        }

        @Test
        @DisplayName("존재하지 않는 허브를 참조하면 404 H-001을 반환한다")
        void missingHub_returns404() throws Exception {
            UUID arrival = createHubAsMaster(uniqueName());

            mockMvc.perform(asRole(post("/api/v1/hub-routes"), "MASTER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(routeBody(UUID.randomUUID().toString(), arrival.toString())))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("H-001"));
        }
    }
}
