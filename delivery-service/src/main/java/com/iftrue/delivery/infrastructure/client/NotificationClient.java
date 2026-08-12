package com.iftrue.delivery.infrastructure.client;

import com.iftrue.delivery.global.common.ApiResponse;
import com.iftrue.delivery.infrastructure.client.config.InternalFeignConfig;
import com.iftrue.delivery.infrastructure.client.dto.NotificationCreateRequest;
import com.iftrue.delivery.infrastructure.client.dto.NotificationCreateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "notification-service",
        configuration = InternalFeignConfig.class
)
public interface NotificationClient {

    @PostMapping("/api/v1/internal/notifications/deliveries")
    ApiResponse<NotificationCreateResponse> createDeliveryNotification(
            @RequestBody NotificationCreateRequest notificationCreateRequest
    );

}
