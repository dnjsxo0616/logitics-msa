package com.iftrue.hub.presentation;

import com.iftrue.hub.application.HubRouteService;
import com.iftrue.hub.application.dto.HubRouteCreateRequestDto;
import com.iftrue.hub.application.dto.HubRouteResponseDto;
import com.iftrue.hub.application.dto.HubRouteUpdateRequestDto;
import com.iftrue.hub.global.response.ApiResponse;
import com.iftrue.hub.global.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hub-routes")
public class HubRouteController {

    private final HubRouteService hubRouteService;

    @PostMapping
    public ResponseEntity<ApiResponse<HubRouteResponseDto>> createHubRoute(
            @Valid @RequestBody HubRouteCreateRequestDto request
    ) {
        HubRouteResponseDto response = hubRouteService.createHubRoute(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<ApiResponse<HubRouteResponseDto>> getHubRoute(@PathVariable UUID routeId) {
        HubRouteResponseDto response = hubRouteService.getHubRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<HubRouteResponseDto>>> getHubRoutes(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<HubRouteResponseDto> response = hubRouteService.getHubRoutes(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<HubRouteResponseDto>>> searchHubRoutes(
            @RequestParam(required = false) UUID departureHubId,
            @RequestParam(required = false) UUID arrivalHubId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<HubRouteResponseDto> response = hubRouteService.searchHubRoutes(
                departureHubId, arrivalHubId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{routeId}")
    public ResponseEntity<ApiResponse<HubRouteResponseDto>> updateHubRoute(
            @PathVariable UUID routeId,
            @Valid @RequestBody HubRouteUpdateRequestDto request
    ) {
        HubRouteResponseDto response = hubRouteService.updateHubRoute(routeId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<ApiResponse<Void>> deleteHubRoute(@PathVariable UUID routeId) {
        hubRouteService.deleteHubRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
