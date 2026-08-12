package com.iftrue.hub.application;

import com.iftrue.hub.application.dto.HubCreateRequestDto;
import com.iftrue.hub.application.dto.HubExistsResponseDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import com.iftrue.hub.application.dto.HubUpdateRequestDto;
import com.iftrue.hub.domain.Hub;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.global.config.CacheConfig;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import com.iftrue.hub.global.response.PageResponse;
import com.iftrue.hub.global.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
public class HubService {

    private static final Set<Integer> ALLOWED_SIZES = Set.of(10, 30, 50);
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "updatedAt", "name");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final HubRepository hubRepository;
    private final CurrentUserProvider currentUserProvider;
    private final HubRouteService hubRouteService;

    @Transactional
    public HubResponseDto createHub(HubCreateRequestDto request) {

        String name = resolveName(request.getName(), null);

        Hub hub = Hub.create(
                name,
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude()
        );

        Hub savedHub = hubRepository.save(hub);

        log.info("[Hub] 허브 생성 완료 id={}", savedHub.getId());

        return HubResponseDto.from(savedHub);
    }

    @Cacheable(cacheNames = CacheConfig.HUB, key = "#hubId")
    public HubResponseDto getHub(UUID hubId) {
        Hub hub = getHubOrThrow(hubId);

        log.info("[Hub] 허브 단건 조회 id={}", hubId);

        return HubResponseDto.from(hub);
    }

    public PageResponse<HubResponseDto> getHubs(Pageable pageable) {
        Page<HubResponseDto> hubPage = hubRepository.findAllByDeletedAtIsNull(toHubPageable(pageable))
                .map(HubResponseDto::from);

        log.info("[Hub] 허브 목록 조회 page={} size={} totalElements={}",
                hubPage.getNumber(), hubPage.getSize(), hubPage.getTotalElements());

        return PageResponse.from(hubPage);
    }

    public PageResponse<HubResponseDto> searchHub(String keyword, Pageable pageable) {
        String searchKeyword = (keyword == null) ? "" : keyword;

        Page<HubResponseDto> hubPage = hubRepository.search(searchKeyword, toHubPageable(pageable))
                .map(HubResponseDto::from);

        log.info("[Hub] 허브 검색 keyword={} totalElements={}", keyword, hubPage.getTotalElements());

        return PageResponse.from(hubPage);
    }

    @CachePut(cacheNames = CacheConfig.HUB, key = "#hubId")
    @Transactional
    public HubResponseDto updateHub(UUID hubId, HubUpdateRequestDto request) {

        Hub hub = getHubOrThrow(hubId);
        String newName = resolveName(request.getName(), hub.getName());

        hub.update(newName, request.getAddress(), request.getLatitude(), request.getLongitude());

        log.info("[Hub] 허브 정보 수정 완료 id={}", hubId);

        return HubResponseDto.from(hub);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.HUB, key = "#hubId"),
            @CacheEvict(cacheNames = CacheConfig.HUB_ROUTE_PATH, allEntries = true)
    })
    @Transactional
    public void deleteHub(UUID hubId) {

        Hub hub = getHubOrThrow(hubId);

        UUID userId = currentUserProvider.getCurrentUserId();
        hub.softDelete(userId);
        hubRouteService.softDeleteRoutesByHub(hubId, userId);

        log.info("[Hub] 허브 삭제 완료 id={}", hubId);
    }

    public HubExistsResponseDto existsHub(UUID hubId) {
        boolean exists = hubRepository.existsByIdAndDeletedAtIsNull(hubId);
        log.info("[Hub-internal] 허브 존재 확인 hubId={}, exists={}", hubId, exists);
        return HubExistsResponseDto.of(hubId, exists);
    }

    private Hub getHubOrThrow(UUID hubId) {
        return hubRepository.findByIdAndDeletedAtIsNull(hubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HUB_NOT_FOUND));
    }

    private String resolveName(String rawName, String currentName) {
        if (rawName == null) {
            return null;
        }

        String normalized = rawName.trim();

        if (!normalized.equals(currentName)
                && hubRepository.existsByNameAndDeletedAtIsNull(normalized)) {
            throw new BusinessException(ErrorCode.HUB_NAME_DUPLICATED);
        }

        return normalized;
    }

    private Pageable toHubPageable(Pageable requestedPageable) {

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
}
