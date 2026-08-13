package com.iftrue.hub.presentation;

import com.iftrue.hub.application.HubRouteService;
import com.iftrue.hub.application.dto.HubRoutePathResponseDto;
import com.iftrue.hub.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/hub-routes")
public class InternalHubRouteController {

    private final HubRouteService hubRouteService;

    @GetMapping("/path")
    public ResponseEntity<ApiResponse<HubRoutePathResponseDto>> getPath(
            @RequestParam UUID departureHubId,
            @RequestParam UUID arrivalHubId
    ) {
        return ResponseEntity.ok(ApiResponse.success(hubRouteService.findPath(departureHubId, arrivalHubId)));
    }
}
