package com.iftrue.order.domain;

import com.iftrue.order.global.exception.BusinessException;
import com.iftrue.order.global.exception.OrderErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_order")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "receiver_company_id", nullable = false)
    private UUID receiverCompanyId;

    @Column(name = "supplier_company_id", nullable = false)
    private UUID supplierCompanyId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "request_message", columnDefinition = "TEXT")
    private String requestMessage;

    @Column(name = "requested_arrival_at", nullable = false)
    private Instant requestedArrivalAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private UUID createdBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    private Order(UUID receiverCompanyId, UUID supplierCompanyId,
                  UUID productId, int quantity, String requestMessage,
                  Instant requestedArrivalAt) {
        this.receiverCompanyId = receiverCompanyId;
        this.supplierCompanyId = supplierCompanyId;
        this.productId = productId;
        this.quantity = quantity;
        this.requestMessage = requestMessage;
        this.requestedArrivalAt = requestedArrivalAt;
        this.status = OrderStatus.PENDING;
    }

    public static Order create(UUID receiverCompanyId, UUID supplierCompanyId,
                               UUID productId, int quantity, String requestMessage,
                               Instant requestedArrivalAt) {
        validateQuantity(quantity);
        validateRequestMessage(requestMessage);
        validateRequestedArrivalAt(requestedArrivalAt);
        return new Order(
                receiverCompanyId,
                supplierCompanyId,
                productId,
                quantity,
                requestMessage,
                requestedArrivalAt
        );
    }

    public void updateRequestMessage(String requestMessage) {
        validateRequestMessageModifiable();
        validateRequestMessage(requestMessage);

        this.requestMessage = requestMessage;
    }

    public void confirm() {
        if (this.status == OrderStatus.CONFIRMED) {
            return;
        }

        changeStatus(OrderStatus.CONFIRMED);
    }
    public void fail() {
        if (this.status == OrderStatus.FAILED) {
            return;
        }

        changeStatus(OrderStatus.FAILED);
    }

    public void cancel() {
        changeStatus(OrderStatus.CANCELED);
    }

    public void complete() {
        if (this.status == OrderStatus.COMPLETED) {
            return;
        }

        changeStatus(OrderStatus.COMPLETED);
    }

    private void changeStatus(OrderStatus requestStatus) {
        validateStatus(requestStatus);
        this.status = requestStatus;
    }

    private void validateStatus(OrderStatus requestStatus) {
        boolean valid = switch (this.status) {
            case PENDING ->
                requestStatus == OrderStatus.CONFIRMED
                        || requestStatus == OrderStatus.FAILED;

            case CONFIRMED ->
                requestStatus == OrderStatus.COMPLETED
                        || requestStatus == OrderStatus.CANCELED;

            case FAILED, CANCELED, COMPLETED -> false;
        };

        if (!valid) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException(OrderErrorCode.INSUFFICIENT_ORDER_QUANTITY);
        }
    }

    private void validateRequestMessageModifiable() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(OrderErrorCode.ORDER_MODIFICATION_NOT_ALLOWED);
        }
    }

    private static void validateRequestMessage(String requestMessage) {
        if (requestMessage != null && requestMessage.isBlank()) {
            throw new BusinessException(OrderErrorCode.INVALID_INPUT);
        }
    }

    private static void validateRequestedArrivalAt(Instant requestedArrivalAt) {
        if (requestedArrivalAt == null) {
            throw new BusinessException(OrderErrorCode.INVALID_REQUESTED_ARRIVAL_TIME);
        }
    }

}
