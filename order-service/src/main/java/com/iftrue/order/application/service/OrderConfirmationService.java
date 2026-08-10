package com.iftrue.order.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderConfirmationService {

    private static final int MAX_ATTEMPTS = 3;

    private final OrderTransactionService orderTransactionService;

    public void confirmWithRetry(UUID orderId) {
        for (int attempt = 1; attempt < MAX_ATTEMPTS; attempt++) {
            try {
                orderTransactionService.confirmOrder(orderId);
                return;

            } catch (TransientDataAccessException ignored) {
            }
        }

        orderTransactionService.confirmOrder(orderId);
    }
}
