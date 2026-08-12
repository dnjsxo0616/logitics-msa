package com.if_true.product.infrastructure.client;

import com.if_true.product.infrastructure.client.dto.HubExistsResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service", configuration = HubClientConfig.class)
public interface HubClient {

	@GetMapping("/api/v1/internal/hubs/{hubId}/exists")
	HubExistsResponse existsHub(@PathVariable("hubId") UUID hubId);
}
