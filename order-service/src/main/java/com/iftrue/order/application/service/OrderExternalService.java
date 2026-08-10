package com.iftrue.order.application.service;

import com.iftrue.order.infrastructure.client.company.CompanyClient;
import com.iftrue.order.infrastructure.client.delivery.DeliveryClient;
import com.iftrue.order.infrastructure.client.delivery.dto.DeliveryCreateRequest;
import com.iftrue.order.infrastructure.client.product.ProductClient;
import com.iftrue.order.infrastructure.client.product.dto.InventoryQuantityRequest;
import com.iftrue.order.infrastructure.client.product.dto.ProductResponse;
import com.iftrue.order.infrastructure.client.user.UserClient;
import com.iftrue.order.infrastructure.client.user.dto.UserResponse;
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

    public void checkCompanyExists(UUID companyId) {
        companyClient.checkCompanyExists(companyId);
    }

    public ProductResponse getProduct(UUID productId) {
        return productClient.getProduct(productId);
    }

    public UserResponse getRecipient(UUID userId) {
        return userClient.getUser(userId);
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

    public void createDelivery(DeliveryCreateRequest request) {
        deliveryClient.createDelivery(request);
    }
}
