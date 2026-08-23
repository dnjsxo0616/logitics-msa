package com.iftrue.order.infrastructure.client.user;

import com.iftrue.order.global.response.ApiResponse;
import com.iftrue.order.infrastructure.client.user.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/internal/users/{userId}")
    ApiResponse<UserResponse> getUser(
            @PathVariable("userId") UUID userId
    );
}
