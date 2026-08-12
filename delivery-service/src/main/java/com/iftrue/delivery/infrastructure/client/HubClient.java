package com.iftrue.delivery.infrastructure.client;

import com.iftrue.delivery.global.common.ApiResponse;
import com.iftrue.delivery.infrastructure.client.config.InternalFeignConfig;
import com.iftrue.delivery.infrastructure.client.dto.HubRouteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "hub-service",
        configuration = InternalFeignConfig.class
)
public interface HubClient {

    @GetMapping("/api/v1/internal/hub-routes/path")
    ApiResponse<HubRouteResponse> getShortestRoute(
            @RequestParam("departureHubId") UUID departureHubId,
            @RequestParam("arrivalHubId") UUID arrivalHubId
    );
}
