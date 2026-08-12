package com.iftrue.user.infrastructure.client.delivery;

import com.iftrue.user.global.response.ApiResponse;
import com.iftrue.user.infrastructure.client.config.InternalFeignConfig;
import com.iftrue.user.infrastructure.client.delivery.dto.DeliveryManagerCreateRequest;
import com.iftrue.user.infrastructure.client.delivery.dto.DeliveryManagerCreateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "delivery-service",
        configuration = InternalFeignConfig.class
)
public interface DeliveryClient {

    @PostMapping("/api/v1/internal/delivery-managers")
    ApiResponse<DeliveryManagerCreateResponse> createDelivery(
            @RequestBody DeliveryManagerCreateRequest request
    );

}