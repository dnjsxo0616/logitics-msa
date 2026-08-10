package com.iftrue.order.infrastructure.client.delivery;

import com.iftrue.order.infrastructure.client.delivery.dto.DeliveryCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @PostMapping("/api/v1/internal/deliveries")
    void createDelivery(
            @RequestBody DeliveryCreateRequest request
    );

    @PostMapping("/api/v1/internal/deliveries/orders/{orderId}/cancel")
    void cancelDelivery(
            @PathVariable("orderId") UUID orderId
    );
}
