package com.iftrue.delivery.presentation.dto.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.UUID;

public record DeliveryCreateRequest(
        @NotNull UUID orderId,
        @NotNull Instant orderedAt,
        @NotNull Instant requestedArrivalAt,
        @NotNull UUID supplierCompanyId,
        @NotNull UUID recipientCompanyId,
        @NotBlank String requesterName,
        @NotBlank String requesterEmail,
        @NotBlank String requesterSlackId,
        @NotNull ProductInfo productInfo,
        String requestMessage
) {
    public DeliveryCreateCommand toCommand() {
        return new DeliveryCreateCommand(
                orderId,
                orderedAt,
                requestedArrivalAt,
                supplierCompanyId,
                recipientCompanyId,
                requesterName,
                requesterEmail,
                requesterSlackId,
                productInfo.productId(),
                productInfo.name(),
                productInfo.quantity(),
                requestMessage
        );
    }

    public record ProductInfo(
            @NotNull UUID productId,
            @NotBlank String name,
            @Positive int quantity
    ) {
    }
}
