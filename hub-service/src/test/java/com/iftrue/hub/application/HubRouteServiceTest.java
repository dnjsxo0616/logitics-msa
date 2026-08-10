package com.iftrue.hub.application;

import com.iftrue.hub.application.dto.HubRouteCreateRequestDto;
import com.iftrue.hub.application.dto.HubRouteResponseDto;
import com.iftrue.hub.application.dto.HubRouteUpdateRequestDto;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.domain.HubRoute;
import com.iftrue.hub.domain.HubRouteRepository;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import com.iftrue.hub.global.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 허브 이동 경로 서비스 테스트")
class HubRouteServiceTest {

    @Mock
    private HubRouteRepository hubRouteRepository;

    @Mock
    private HubRepository hubRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private HubRouteService hubRouteService;

    @Test
    @DisplayName("출발 허브와 도착 허브가 같으면 H-005 예외가 발생하고 저장하지 않는다")
    void createRouteFailsWhenDepartureAndArrivalAreSame() {
        UUID hubId = UUID.randomUUID();
        HubRouteCreateRequestDto request = createRequest(hubId, hubId, 120, "325.50");
        given(currentUserProvider.getCurrentUserRole()).willReturn("MASTER");

        assertThatThrownBy(() -> hubRouteService.createHubRoute(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.HUB_ROUTE_SAME_ENDPOINT.getMessage());

        verify(hubRouteRepository, never()).save(any(HubRoute.class));
    }

    @Test
    @DisplayName("동일한 출발-도착 이동 경로가 이미 존재하면 H-004 예외가 발생하고 저장하지 않는다")
    void createRouteFailsWhenActiveRoutePairAlreadyExists() {
        UUID departureHubId = UUID.randomUUID();
        UUID arrivalHubId = UUID.randomUUID();
        HubRouteCreateRequestDto request = createRequest(departureHubId, arrivalHubId, 120, "325.50");
        given(currentUserProvider.getCurrentUserRole()).willReturn("MASTER");
        given(hubRepository.existsByIdAndDeletedAtIsNull(departureHubId)).willReturn(true);
        given(hubRepository.existsByIdAndDeletedAtIsNull(arrivalHubId)).willReturn(true);
        given(hubRouteRepository.existsByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(
                departureHubId, arrivalHubId)).willReturn(true);

        assertThatThrownBy(() -> hubRouteService.createHubRoute(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.HUB_ROUTE_DUPLICATED.getMessage());

        verify(hubRouteRepository, never()).save(any(HubRoute.class));
    }

    @Test
    @DisplayName("허용되지 않은 정렬 필드는 기본 정렬(createdAt DESC)로 보정된다")
    void unsupportedSortFieldFallsBackToCreatedAtDescending() {
        given(hubRouteRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of()));
        Pageable request = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "distanceKm"));

        hubRouteService.getHubRoutes(request);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(hubRouteRepository).findAllByDeletedAtIsNull(pageableCaptor.capture());
        Pageable appliedPageable = pageableCaptor.getValue();

        Sort.Order defaultSort = appliedPageable.getSort().getOrderFor("createdAt");
        assertThat(defaultSort).isNotNull();
        assertThat(defaultSort.getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(appliedPageable.getSort().getOrderFor("distanceKm")).isNull();
    }

    @Test
    @DisplayName("검색 시 허용되지 않은 size는 기본 size(10)로 보정된다")
    void searchClampsDisallowedPageSizeToDefault() {
        given(hubRouteRepository.search(any(), any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of()));

        hubRouteService.searchHubRoutes(null, null, PageRequest.of(0, 25));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(hubRouteRepository).search(any(), any(), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("이동 경로 수정 시 전달된 필드만 반영하고 미전송 필드는 기존 값을 유지한다")
    void updateRouteKeepsUntouchedFields() {
        UUID routeId = UUID.randomUUID();
        HubRoute route = routeWithId(routeId, 300, "325.50");
        given(currentUserProvider.getCurrentUserRole()).willReturn("MASTER");
        given(hubRouteRepository.findByIdAndDeletedAtIsNull(routeId)).willReturn(Optional.of(route));

        HubRouteUpdateRequestDto request = updateRequest(null, "201.10");

        HubRouteResponseDto response = hubRouteService.updateHubRoute(routeId, request);

        assertThat(response.durationMinutes()).isEqualTo(300);
        assertThat(response.distanceKm()).isEqualByComparingTo("201.10");
    }

    private HubRouteCreateRequestDto createRequest(
            UUID departureHubId, UUID arrivalHubId, Integer durationMinutes, String distanceKm) {
        HubRouteCreateRequestDto request = new HubRouteCreateRequestDto();
        ReflectionTestUtils.setField(request, "departureHubId", departureHubId);
        ReflectionTestUtils.setField(request, "arrivalHubId", arrivalHubId);
        ReflectionTestUtils.setField(request, "durationMinutes", durationMinutes);
        ReflectionTestUtils.setField(request, "distanceKm", new BigDecimal(distanceKm));
        return request;
    }

    private HubRoute routeWithId(UUID id, Integer durationMinutes, String distanceKm) {
        HubRoute route = HubRoute.create(
                UUID.randomUUID(), UUID.randomUUID(), durationMinutes, new BigDecimal(distanceKm));
        ReflectionTestUtils.setField(route, "id", id);
        return route;
    }

    private HubRouteUpdateRequestDto updateRequest(Integer durationMinutes, String distanceKm) {
        HubRouteUpdateRequestDto request = new HubRouteUpdateRequestDto();
        ReflectionTestUtils.setField(request, "durationMinutes", durationMinutes);
        ReflectionTestUtils.setField(request, "distanceKm",
                distanceKm == null ? null : new BigDecimal(distanceKm));
        return request;
    }
}
