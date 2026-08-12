package com.iftrue.notification.infrastructure.client.order;

import com.iftrue.notification.global.response.ApiResponse;
import com.iftrue.notification.infrastructure.client.order.dto.OrderNotificationContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/api/v1/internal/orders/{orderId}/notification-context")
    ApiResponse<OrderNotificationContext> getNotificationContext(
            @PathVariable("orderId") UUID orderId
    );
}
