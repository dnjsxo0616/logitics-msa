package com.iftrue.hub.presentation;

import com.iftrue.hub.application.HubService;
import com.iftrue.hub.application.dto.HubCreateRequestDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import com.iftrue.hub.application.dto.HubUpdateRequestDto;
import com.iftrue.hub.global.response.ApiResponse;
import com.iftrue.hub.global.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
public class HubController {

    private final HubService hubService;

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

    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponseDto>> getHub(@PathVariable UUID hubId) {
        HubResponseDto response = hubService.getHub(hubId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<HubResponseDto>>> getHubs(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<HubResponseDto> response = hubService.getHubs(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<HubResponseDto>>> searchHub(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<HubResponseDto> response = hubService.searchHub(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponseDto>> updateHub(
            @PathVariable UUID hubId,
            @Valid @RequestBody HubUpdateRequestDto request
    ) {
        HubResponseDto response = hubService.updateHub(hubId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/{hubId}")
    public ResponseEntity<ApiResponse<Void>> deleteHub(@PathVariable UUID hubId) {
        hubService.deleteHub(hubId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
