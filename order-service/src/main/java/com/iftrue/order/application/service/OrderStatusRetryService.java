package com.iftrue.order.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderStatusRetryService {

    private static final int MAX_ATTEMPTS = 3;

    private final OrderTransactionService orderTransactionService;

    public void confirmWithRetry(UUID orderId) { executeWithRetry(() -> orderTransactionService.confirmOrder(orderId));}

    public void failWithRetry(UUID orderId) {
        executeWithRetry(() -> orderTransactionService.failOrder(orderId));
    }

    private void executeWithRetry(Runnable statusChange) {
        for (int attempt = 1; attempt < MAX_ATTEMPTS; attempt++) {
            try {
                statusChange.run();
                return;

            } catch (TransientDataAccessException ignored) {}
        }

        statusChange.run();
    }
}
