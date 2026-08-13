package com.iftrue.hub.presentation;

import com.iftrue.hub.application.HubService;
import com.iftrue.hub.application.dto.HubCreateRequestDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import com.iftrue.hub.application.dto.HubUpdateRequestDto;
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

@Tag(name = "Hub", description = "허브 관리 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
public class HubController {

    private final HubService hubService;

    @Operation(summary = "허브 등록", description = "새 허브를 등록합니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<ApiResponse<HubResponseDto>> createHub(
            @Valid @RequestBody HubCreateRequestDto request
    ) {
        HubResponseDto response = hubService.createHub(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @Operation(summary = "허브 단건 조회", description = "허브 ID로 단건을 조회합니다.")
    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponseDto>> getHub(
            @Parameter(description = "허브 ID") @PathVariable UUID hubId
    ) {
        HubResponseDto response = hubService.getHub(hubId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "허브 목록 조회", description = "허브 목록을 페이징 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<HubResponseDto>>> getHubs(
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<HubResponseDto> response = hubService.getHubs(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "허브 검색", description = "이름 키워드로 허브를 검색·페이징 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<HubResponseDto>>> searchHub(
            @Parameter(description = "허브 이름 검색어") @RequestParam(required = false) String keyword,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<HubResponseDto> response = hubService.searchHub(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "허브 수정", description = "허브 정보를 부분 수정합니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponseDto>> updateHub(
            @Parameter(description = "허브 ID") @PathVariable UUID hubId,
            @Valid @RequestBody HubUpdateRequestDto request
    ) {
        HubResponseDto response = hubService.updateHub(hubId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "허브 삭제", description = "허브를 삭제(soft delete)합니다. 연관 이동 경로도 함께 삭제됩니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/{hubId}")
    public ResponseEntity<ApiResponse<Void>> deleteHub(
            @Parameter(description = "허브 ID") @PathVariable UUID hubId
    ) {
        hubService.deleteHub(hubId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
