package com.iftrue.order.infrastructure.client.product;

import com.iftrue.order.global.response.ApiResponse;
import com.iftrue.order.infrastructure.client.product.dto.InventoryQuantityRequest;
import com.iftrue.order.infrastructure.client.product.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/v1/internal/products/{productId}")
    ApiResponse<ProductResponse> getProduct(
            @PathVariable("productId") UUID productId
    );

    @PostMapping("/api/v1/internal/inventories/{productId}/decrease")
    void decreaseInventory(
            @PathVariable("productId") UUID productId,
            @RequestBody InventoryQuantityRequest request
    );

    @PostMapping("/api/v1/internal/inventories/{productId}/restore")
    void restoreInventory(
            @PathVariable("productId") UUID productId,
            @RequestBody InventoryQuantityRequest request
    );
}
