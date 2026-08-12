package com.iftrue.delivery.application.dto.delivery;

import java.time.Instant;
import java.util.UUID;

public record DeliveryCreateCommand(
        UUID orderId,
        Instant orderedAt,
        Instant requestedArrivalAt,
        UUID supplierCompanyId,
        UUID recipientCompanyId,
        String requesterName,
        String requesterEmail,
        String requesterSlackId,
        UUID productId,
        String productName,
        int productQuantity,
        String requestMessage
) {
}
