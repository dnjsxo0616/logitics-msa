package com.iftrue.order.presentation.controller;

import com.iftrue.order.application.service.OrderNotificationContextService;
import com.iftrue.order.global.response.ApiResponse;
import com.iftrue.order.presentation.dto.OrderNotificationContextResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/orders")
public class InternalOrderController {

    private final OrderNotificationContextService orderNotificationContextService;

    @GetMapping("/{orderId}/notification-context")
    public ResponseEntity<ApiResponse<OrderNotificationContextResponse>> getNotificationContext(
            @PathVariable UUID orderId
    ) {
        OrderNotificationContextResponse response = orderNotificationContextService.getNotificationContext(orderId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
