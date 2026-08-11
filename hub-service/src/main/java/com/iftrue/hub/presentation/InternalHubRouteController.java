package com.iftrue.hub.presentation;

import com.iftrue.hub.application.HubRouteService;
import com.iftrue.hub.application.dto.HubRoutePathResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public HubRoutePathResponseDto getPath(
            @RequestParam UUID departureHubId,
            @RequestParam UUID arrivalHubId
    ) {
        return hubRouteService.findPath(departureHubId, arrivalHubId);
    }
}
