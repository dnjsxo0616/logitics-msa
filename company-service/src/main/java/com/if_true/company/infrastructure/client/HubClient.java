package com.if_true.company.infrastructure.client;

import com.if_true.company.infrastructure.client.dto.HubResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service")
public interface HubClient {

	@GetMapping("/api/v1/hubs/{id}")
	HubResponse getHub(@PathVariable UUID id);
}
