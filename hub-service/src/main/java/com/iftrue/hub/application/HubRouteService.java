package com.iftrue.hub.application;

import com.iftrue.hub.application.dto.HubRouteCreateRequestDto;
import com.iftrue.hub.application.dto.HubRouteResponseDto;
import com.iftrue.hub.application.dto.HubRouteUpdateRequestDto;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.domain.HubRoute;
import com.iftrue.hub.domain.HubRouteRepository;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import com.iftrue.hub.global.response.PageResponse;
import com.iftrue.hub.global.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubRouteService {

    private static final String ROLE_MASTER = "MASTER";
    private static final Set<Integer> ALLOWED_SIZES = Set.of(10, 30, 50);
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "updatedAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public HubRouteResponseDto createHubRoute(HubRouteCreateRequestDto request) {
        checkMasterRole();

        UUID departureHubId = request.getDepartureHubId();
        UUID arrivalHubId = request.getArrivalHubId();

        if (departureHubId.equals(arrivalHubId)) {
            throw new BusinessException(ErrorCode.HUB_ROUTE_SAME_ENDPOINT);
        }

        validateHubExists(departureHubId);
        validateHubExists(arrivalHubId);

        if (hubRouteRepository.existsByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(
                departureHubId, arrivalHubId)) {
            throw new BusinessException(ErrorCode.HUB_ROUTE_DUPLICATED);
        }

        HubRoute hubRoute = HubRoute.create(
                departureHubId,
                arrivalHubId,
                request.getDurationMinutes(),
                request.getDistanceKm()
        );

        HubRoute savedHubRoute = hubRouteRepository.save(hubRoute);

        log.info("[HubRoute] 허브 이동 경로 생성 완료 id={}", savedHubRoute.getId());

        return HubRouteResponseDto.from(savedHubRoute);
    }

    public HubRouteResponseDto getHubRoute(UUID routeId) {
        HubRoute hubRoute = getHubRouteOrThrow(routeId);

        log.info("[HubRoute] 허브 이동 경로 단건 조회 id={}", routeId);

        return HubRouteResponseDto.from(hubRoute);
    }

    public PageResponse<HubRouteResponseDto> getHubRoutes(Pageable pageable) {
        Page<HubRouteResponseDto> hubRoutePage = hubRouteRepository.findAllByDeletedAtIsNull(toRoutePageable(pageable))
                .map(HubRouteResponseDto::from);

        log.info("[HubRoute] 허브 이동 경로 목록 조회 page={}, size={}, totalElements={}",
                hubRoutePage.getNumber(), hubRoutePage.getSize(), hubRoutePage.getTotalElements());

        return PageResponse.from(hubRoutePage);
    }

    public PageResponse<HubRouteResponseDto> searchHubRoutes(
            UUID departureHubId, UUID arrivalHubId, Pageable pageable) {

        Page<HubRouteResponseDto> hubRoutePage = hubRouteRepository.search(departureHubId, arrivalHubId, toRoutePageable(pageable))
                .map(HubRouteResponseDto::from);

        log.info("[HubRoute] 허브 이동 경로 검색 departureHubId={}, arrivalHubId={}, totalElements={}",
                departureHubId, arrivalHubId, hubRoutePage.getTotalElements());

        return PageResponse.from(hubRoutePage);
    }

    @Transactional
    public HubRouteResponseDto updateHubRoute(UUID routeId, HubRouteUpdateRequestDto request) {
        checkMasterRole();

        HubRoute hubRoute = getHubRouteOrThrow(routeId);
        hubRoute.update(request.getDurationMinutes(), request.getDistanceKm());

        log.info("[HubRoute] 허브 이동 경로 정보 수정 완료 id={}", routeId);

        return HubRouteResponseDto.from(hubRoute);
    }

    @Transactional
    public void deleteHubRoute(UUID routeId) {
        checkMasterRole();
        HubRoute hubRoute = getHubRouteOrThrow(routeId);
        hubRoute.softDelete(currentUserProvider.getCurrentUserId());

        log.info("[HubRoute] 허브 이동 경로 삭제 완료 id={}", routeId);
    }

    @Transactional
    public void softDeleteRoutesByHub(UUID hubId, UUID userId) {
        List<HubRoute> routes = hubRouteRepository.findActiveRoutesByHubId(hubId);
        routes.forEach(route -> route.softDelete(userId));

        log.info("[HubRoute] 허브 삭제 시 연관 경로 soft delete 완료 hubId={}, count={}", hubId, routes.size());
    }

    private Pageable toRoutePageable(Pageable requestedPageable) {

        int pageSize = ALLOWED_SIZES.contains(requestedPageable.getPageSize())
                ? requestedPageable.getPageSize()
                : DEFAULT_PAGE_SIZE;

        List<Sort.Order> validSortOrders = requestedPageable.getSort()
                .stream()
                .filter(order -> ALLOWED_SORT.contains(order.getProperty()))
                .toList();

        Sort resolvedSort = validSortOrders.isEmpty()
                ? DEFAULT_SORT
                : Sort.by(validSortOrders);

        return PageRequest.of(
                requestedPageable.getPageNumber(),
                pageSize,
                resolvedSort
        );
    }

    private HubRoute getHubRouteOrThrow(UUID routeId) {
        return hubRouteRepository.findByIdAndDeletedAtIsNull(routeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HUB_ROUTE_NOT_FOUND));
    }

    private void validateHubExists(UUID hubId) {
        if (!hubRepository.existsByIdAndDeletedAtIsNull(hubId)) {
            throw new BusinessException(ErrorCode.HUB_NOT_FOUND);
        }
    }

    private void checkMasterRole() {
        if (!ROLE_MASTER.equals(currentUserProvider.getCurrentUserRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
