package com.iftrue.delivery.infrastructure.client;

import com.iftrue.delivery.global.common.ApiResponse;
import com.iftrue.delivery.infrastructure.client.config.InternalFeignConfig;
import com.iftrue.delivery.infrastructure.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "user-service",
        configuration = InternalFeignConfig.class
)
public interface UserClient {

    @GetMapping("/api/v1/internal/users/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") UUID userId);
}
