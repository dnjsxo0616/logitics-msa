package com.iftrue.hub.presentation;

import com.iftrue.hub.application.HubService;
import com.iftrue.hub.application.dto.HubExistsResponseDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public HubResponseDto getHub(@PathVariable UUID hubId) {
        return hubService.getHub(hubId);
    }

    @GetMapping("/{hubId}/exists")
    public HubExistsResponseDto existsHub(@PathVariable UUID hubId) {
        return hubService.existsHub(hubId);
    }
}
