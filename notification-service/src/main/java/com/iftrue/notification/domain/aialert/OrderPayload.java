package com.iftrue.notification.domain.aialert;

import java.time.Instant;

public record OrderPayload(
        Instant orderedAt,
        String requesterName,
        String requesterEmail,
        String productName,
        int quantity,
        String requestMessage,
        String status
) {
}
