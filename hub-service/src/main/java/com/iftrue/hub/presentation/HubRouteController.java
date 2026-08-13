package com.iftrue.hub.presentation;

import com.iftrue.hub.application.HubRouteService;
import com.iftrue.hub.application.dto.HubRouteAutoCreateRequestDto;
import com.iftrue.hub.application.dto.HubRouteCreateRequestDto;
import com.iftrue.hub.application.dto.HubRouteResponseDto;
import com.iftrue.hub.application.dto.HubRouteUpdateRequestDto;
import com.iftrue.hub.global.response.ApiResponse;
import com.iftrue.hub.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "HubRoute", description = "허브 간 이동 경로 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hub-routes")
public class HubRouteController {

    private final HubRouteService hubRouteService;

    @Operation(summary = "이동 경로 등록", description = "출발-도착 허브 간 이동 경로를 등록합니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<ApiResponse<HubRouteResponseDto>> createHubRoute(
            @Valid @RequestBody HubRouteCreateRequestDto request
    ) {
        HubRouteResponseDto response = hubRouteService.createHubRoute(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @Operation(summary = "이동 경로 자동 등록", description = "출발-도착 허브 좌표로 TMap을 조회해 소요시간·거리를 자동 산출하여 경로를 등록합니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/auto")
    public ResponseEntity<ApiResponse<HubRouteResponseDto>> createHubRouteAuto(
            @Valid @RequestBody HubRouteAutoCreateRequestDto request
    ) {
        HubRouteResponseDto response = hubRouteService.createHubRouteAuto(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @Operation(summary = "이동 경로 단건 조회", description = "경로 ID로 단건을 조회합니다.")
    @GetMapping("/{routeId}")
    public ResponseEntity<ApiResponse<HubRouteResponseDto>> getHubRoute(
            @Parameter(description = "이동 경로 ID") @PathVariable UUID routeId
    ) {
        HubRouteResponseDto response = hubRouteService.getHubRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "이동 경로 목록 조회", description = "이동 경로 목록을 페이징 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<HubRouteResponseDto>>> getHubRoutes(
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<HubRouteResponseDto> response = hubRouteService.getHubRoutes(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "이동 경로 검색", description = "출발/도착 허브로 이동 경로를 검색·페이징 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<HubRouteResponseDto>>> searchHubRoutes(
            @Parameter(description = "출발 허브 ID") @RequestParam(required = false) UUID departureHubId,
            @Parameter(description = "도착 허브 ID") @RequestParam(required = false) UUID arrivalHubId,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<HubRouteResponseDto> response = hubRouteService.searchHubRoutes(
                departureHubId, arrivalHubId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "이동 경로 수정", description = "이동 경로의 소요시간·거리를 수정합니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/{routeId}")
    public ResponseEntity<ApiResponse<HubRouteResponseDto>> updateHubRoute(
            @Parameter(description = "이동 경로 ID") @PathVariable UUID routeId,
            @Valid @RequestBody HubRouteUpdateRequestDto request
    ) {
        HubRouteResponseDto response = hubRouteService.updateHubRoute(routeId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "이동 경로 삭제", description = "이동 경로를 삭제(soft delete)합니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/{routeId}")
    public ResponseEntity<ApiResponse<Void>> deleteHubRoute(
            @Parameter(description = "이동 경로 ID") @PathVariable UUID routeId
    ) {
        hubRouteService.deleteHubRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
