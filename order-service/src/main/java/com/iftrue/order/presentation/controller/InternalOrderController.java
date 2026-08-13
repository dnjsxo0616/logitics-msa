package com.iftrue.order.presentation.controller;

import com.iftrue.order.application.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/orders")
public class InternalOrderController {

    private final OrderTransactionService orderTransactionService;

    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<Void> complete(@PathVariable UUID orderId) {
        orderTransactionService.completeOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
