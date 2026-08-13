package com.iftrue.order.presentation.controller;

import com.iftrue.order.application.service.OrderService;
import com.iftrue.order.global.security.AuthenticatedUser;
import com.iftrue.order.presentation.controller.api.OrderApi;
import com.iftrue.order.presentation.dto.OrderCreateRequest;
import com.iftrue.order.presentation.dto.OrderCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @Override
    @PreAuthorize("hasAnyRole('MASTER', 'SUPPLIER_MANAGER')")
    public ResponseEntity<OrderCreateResponse> createOrder(
            OrderCreateRequest request,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        OrderCreateResponse response = orderService.createOrder(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
