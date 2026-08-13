package com.iftrue.delivery.presentation;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateResult;
import com.iftrue.delivery.application.dto.delivery.DeliveryResult;
import com.iftrue.delivery.application.service.delivery.DeliveryCommandService;
import com.iftrue.delivery.application.service.delivery.DeliveryCreateService;
import com.iftrue.delivery.application.service.delivery.DeliveryQueryService;
import com.iftrue.delivery.global.common.ApiResponse;
import com.iftrue.delivery.presentation.dto.delivery.DeliveryCreateRequest;
import com.iftrue.delivery.presentation.dto.delivery.DeliveryCreateResponse;
import com.iftrue.delivery.presentation.dto.delivery.DeliveryResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/deliveries")
public class InternalDeliveryController {
    private final DeliveryCreateService deliveryCreateService;
    private final DeliveryCommandService deliveryCommandService;
    private final DeliveryQueryService deliveryQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryCreateResponse>> createDelivery(@Valid @RequestBody DeliveryCreateRequest request) {
        DeliveryCreateResult deliveryCreateResult = deliveryCreateService.create(request.toCommand());
        DeliveryCreateResponse deliveryCreateResponse = DeliveryCreateResponse.from(deliveryCreateResult);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), deliveryCreateResponse));
    }

    @PostMapping("/{deliveryId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelDelivery(@PathVariable("deliveryId") UUID deliveryId) {
        deliveryCommandService.cancel(deliveryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DeliveryResponse>> getDeliveryByOrderId(@NotNull @RequestParam("orderId") UUID orderId) {
        DeliveryResult deliveryResult =
                deliveryQueryService.getDeliveryByOrderId(orderId);
        DeliveryResponse response =
                DeliveryResponse.from(deliveryResult);
        return ResponseEntity.ok()
                .body(ApiResponse.success(HttpStatus.OK.value(), response));
    }
}
