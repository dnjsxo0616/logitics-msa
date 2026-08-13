package com.iftrue.hub.presentation;

import com.iftrue.hub.application.HubService;
import com.iftrue.hub.application.dto.HubExistsResponseDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import com.iftrue.hub.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/hubs")
public class InternalHubController {

    private final HubService hubService;

    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponseDto>> getHub(@PathVariable UUID hubId) {
        return ResponseEntity.ok(ApiResponse.success(hubService.getHub(hubId)));
    }

    @GetMapping("/{hubId}/exists")
    public ResponseEntity<ApiResponse<HubExistsResponseDto>> existsHub(@PathVariable UUID hubId) {
        return ResponseEntity.ok(ApiResponse.success(hubService.existsHub(hubId)));
    }
}
