package com.iftrue.delivery.presentation;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateResult;
import com.iftrue.delivery.application.service.delivery.DeliveryCreateService;
import com.iftrue.delivery.global.common.ApiResponse;
import com.iftrue.delivery.global.common.CursorResponse;
import com.iftrue.delivery.presentation.dto.delivery.DeliveryCreateRequest;
import com.iftrue.delivery.presentation.dto.delivery.DeliveryCreateResponse;
import com.iftrue.delivery.presentation.dto.delivery.DeliveryIdResponse;
import com.iftrue.delivery.presentation.dto.delivery.DeliveryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DeliveryController {
    private final DeliveryCreateService deliveryCreateService;

    @GetMapping("/deliveries/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getDelivery(@PathVariable("deliveryId") UUID deliveryId) {
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deliveries")
    public ResponseEntity<ApiResponse<CursorResponse<DeliveryResponse>>> getDeliveries() {
        return ResponseEntity.ok().body(null);
    }

    @PatchMapping("/deliveries/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryIdResponse>> updateDelivery(@PathVariable("deliveryId") UUID deliveryId) {
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/deliveries/{deliveryId}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable("deliveryId") UUID deliveryId) {
        return ResponseEntity.noContent().build();
    }

}
