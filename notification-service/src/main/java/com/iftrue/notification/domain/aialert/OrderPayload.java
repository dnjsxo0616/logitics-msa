package com.iftrue.notification.domain.aialert;

import java.time.Instant;
import java.util.UUID;

public record OrderPayload(
        Instant orderedAt,
        Instant requestedArrivalAt,
        UUID requesterUserId,
        UUID receiverCompanyId,
        UUID supplierCompanyId,
        String requesterName,
        String requesterEmail,
        String productName,
        int quantity,
        String requestMessage,
        String status
) {
}
