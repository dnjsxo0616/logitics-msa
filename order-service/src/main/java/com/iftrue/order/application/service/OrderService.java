package com.iftrue.order.application.service;

import com.iftrue.order.global.exception.BusinessException;
import com.iftrue.order.global.exception.OrderErrorCode;
import com.iftrue.order.global.security.AuthenticatedUser;
import com.iftrue.order.infrastructure.client.delivery.dto.DeliveryCreateRequest;
import com.iftrue.order.infrastructure.client.product.dto.ProductResponse;
import com.iftrue.order.infrastructure.client.user.dto.UserResponse;
import com.iftrue.order.presentation.dto.OrderCreateRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String MASTER_ROLE = "MASTER";

    private final OrderTransactionService orderTransactionService;
    private final OrderConfirmationService orderConfirmationService;
    private final OrderExternalService orderExternalService;

    public void createOrder(OrderCreateRequest request, AuthenticatedUser user) {
        validateCompanyScope(request, user);
        UUID recipientUserId = resolveRecipientUserId(request, user);

        UUID orderId = orderTransactionService.createPendingOrder(request);

        boolean inventoryDecreased = false;
        boolean deliveryCreated = false;

        try {
            orderExternalService.checkCompanyExists(request.receiverCompanyId());
            orderExternalService.checkCompanyExists(request.supplierCompanyId());

            ProductResponse product = orderExternalService.getProduct(request.productId());
            validateProductInfo(product);

            UserResponse recipient = orderExternalService.getRecipient(recipientUserId);

            validateRecipientInfo(recipient);
            validateProductSupplier(request, product);
            validateRecipientCompany(request, recipient);

            orderExternalService.decreaseInventory(orderId, request.productId(), request.quantity());

            inventoryDecreased = true;

            DeliveryCreateRequest deliveryRequest = createDeliveryRequest(
                    orderId,
                    request,
                    recipient
            );

            orderExternalService.createDelivery(deliveryRequest);

            deliveryCreated = true;

            orderConfirmationService.confirmWithRetry(orderId);

        } catch (RuntimeException exception) {
            RuntimeException failure = exception;

            if (deliveryCreated) {
                failure = cancelDeliveryAndRestoreInventory(orderId, request, exception);
            } else if (inventoryDecreased) {
                failure = restoreInventory(orderId, request, exception);
            }

            orderTransactionService.failOrder(orderId);

            throw convertExternalException(failure);
        }
    }

    private void validateCompanyScope(OrderCreateRequest request, AuthenticatedUser user) {
        if (MASTER_ROLE.equals(user.role())) {
            return;
        }

        if (user.companyId() == null || !user.companyId().equals(request.receiverCompanyId())) {
            throw new BusinessException(OrderErrorCode.ACCESS_DENIED);
        }
    }

    private UUID resolveRecipientUserId(OrderCreateRequest request, AuthenticatedUser user) {
        if (MASTER_ROLE.equals(user.role())) {
            if (request.recipientUserId() == null) {
                throw new BusinessException(OrderErrorCode.RECIPIENT_REQUIRED);
            }

            return request.recipientUserId();
        }

        if (request.recipientUserId() != null && !request.recipientUserId().equals(user.userId())) {
            throw new BusinessException(OrderErrorCode.ACCESS_DENIED);
        }

        return user.userId();
    }

    private void validateProductInfo(ProductResponse product) {
        if (product == null || product.companyId() == null) {
            throw new BusinessException(OrderErrorCode.INVALID_PRODUCT_INFO);
        }
    }

    private void validateRecipientInfo(UserResponse recipient) {
        if (recipient == null
                || recipient.companyId() == null
                || !StringUtils.hasText(recipient.name())
                || !StringUtils.hasText(recipient.slackId())) {
            throw new BusinessException(OrderErrorCode.INVALID_RECIPIENT_INFO);
        }
    }

    private void validateProductSupplier(OrderCreateRequest request, ProductResponse product
    ) {
        if (!request.supplierCompanyId().equals(product.companyId())) {
            throw new BusinessException(OrderErrorCode.PRODUCT_SUPPLIER_MISMATCH);
        }
    }

    private void validateRecipientCompany(OrderCreateRequest request, UserResponse recipient
    ) {
        if (!request.receiverCompanyId().equals(recipient.companyId())) {
            throw new BusinessException(OrderErrorCode.RECIPIENT_COMPANY_MISMATCH);
        }
    }

    private DeliveryCreateRequest createDeliveryRequest(
            UUID orderId,
            OrderCreateRequest request,
            UserResponse recipient
    ) {
        return new DeliveryCreateRequest(
                orderId,
                request.supplierCompanyId(),
                request.receiverCompanyId(),
                recipient.name(),
                recipient.slackId()
        );
    }

    private RuntimeException restoreInventory(UUID orderId, OrderCreateRequest request, RuntimeException originalException) {
        try {
            orderExternalService.restoreInventory(orderId, request.productId(), request.quantity());

            return originalException;

        } catch (RuntimeException restoreException) {
            BusinessException failure = new BusinessException(OrderErrorCode.INVENTORY_RESTORE_FAILED);

            failure.addSuppressed(originalException);
            failure.addSuppressed(restoreException);

            return failure;
        }
    }

    private RuntimeException cancelDeliveryAndRestoreInventory(
            UUID orderId,
            OrderCreateRequest request,
            RuntimeException originalException
    ) {
        try {
            orderExternalService.cancelDelivery(orderId);

        } catch (RuntimeException cancellationException) {
            BusinessException failure = new BusinessException(OrderErrorCode.DELIVERY_CANCELLATION_FAILED);

            failure.addSuppressed(originalException);
            failure.addSuppressed(cancellationException);

            return failure;
        }

        return restoreInventory(orderId, request, originalException);
    }

    private RuntimeException convertExternalException(
            RuntimeException exception
    ) {
        if (exception instanceof BusinessException) {
            return exception;
        }

        if (exception instanceof FeignException) {
            return new BusinessException(OrderErrorCode.EXTERNAL_SERVICE_CALL_FAILED);
        }

        return exception;
    }
}
