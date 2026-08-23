package com.iftrue.order.application.service;

import com.iftrue.order.global.exception.BusinessException;
import com.iftrue.order.global.exception.OrderErrorCode;
import com.iftrue.order.global.response.ApiResponse;
import com.iftrue.order.infrastructure.client.company.CompanyClient;
import com.iftrue.order.infrastructure.client.delivery.DeliveryClient;
import com.iftrue.order.infrastructure.client.delivery.dto.DeliveryCreateRequest;
import com.iftrue.order.infrastructure.client.delivery.dto.DeliveryCreateResponse;
import com.iftrue.order.infrastructure.client.product.ProductClient;
import com.iftrue.order.infrastructure.client.product.dto.InventoryQuantityRequest;
import com.iftrue.order.infrastructure.client.product.dto.ProductResponse;
import com.iftrue.order.infrastructure.client.user.UserClient;
import com.iftrue.order.infrastructure.client.user.dto.UserResponse;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderExternalService {

    private final CompanyClient companyClient;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final DeliveryClient deliveryClient;
    private final Validator validator;

    public void checkCompanyExists(UUID companyId) {
        companyClient.checkCompanyExists(companyId);
    }

    public ProductResponse getProduct(UUID productId) {
        ProductResponse response = productClient.getProduct(productId);

        return validateResponse(response, OrderErrorCode.INVALID_PRODUCT_INFO);
    }

    public UserResponse getRecipient(UUID userId) {
        ApiResponse<UserResponse> apiResponse = userClient.getUser(userId);
        UserResponse response = apiResponse == null
                ? null
                : apiResponse.getData();

        return validateResponse(response, OrderErrorCode.INVALID_RECIPIENT_INFO);
    }

    public void decreaseInventory(UUID orderId, UUID productId, int quantity
    ) {
        InventoryQuantityRequest request = new InventoryQuantityRequest(orderId, quantity);

        productClient.decreaseInventory(productId, request);
    }

    public void restoreInventory(UUID orderId, UUID productId, int quantity
    ) {
        InventoryQuantityRequest request = new InventoryQuantityRequest(orderId, quantity);

        productClient.restoreInventory(productId, request);
    }

    public UUID createDelivery(DeliveryCreateRequest request) {
        ApiResponse<DeliveryCreateResponse> response = deliveryClient.createDelivery(request);

        if (response == null || response.getData() == null) {
            return null;
        }

        return response.getData().deliveryId();
    }

    public void cancelDelivery(UUID deliveryId) {
        deliveryClient.cancelDelivery(deliveryId);
    }

    private <T> T validateResponse(T response, OrderErrorCode errorCode) {
        if (response == null || !validator.validate(response).isEmpty()) {
            throw new BusinessException(errorCode);
        }

        return response;
    }
}
