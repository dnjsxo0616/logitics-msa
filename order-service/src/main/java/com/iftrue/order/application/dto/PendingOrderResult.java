package com.iftrue.order.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PendingOrderResult(
        UUID orderId,
        Instant orderedAt
) {
}
