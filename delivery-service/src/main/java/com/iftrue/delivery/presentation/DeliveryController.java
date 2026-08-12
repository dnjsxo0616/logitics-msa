package com.iftrue.delivery.presentation;

import com.iftrue.delivery.application.service.delivery.DeliveryCommandService;
import com.iftrue.delivery.global.common.ApiResponse;
import com.iftrue.delivery.global.common.CursorResponse;
import com.iftrue.delivery.presentation.dto.delivery.DeliveryIdResponse;
import com.iftrue.delivery.presentation.dto.delivery.DeliveryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DeliveryController {

    private final DeliveryCommandService deliveryCommandService;

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

    @PatchMapping("/deliveries/{deliveryId}/routes/{routeId}/departure")
    public ResponseEntity<ApiResponse<Void>> departDeliveryRoute(
            @PathVariable("deliveryId") UUID deliveryId,
            @PathVariable("routeId") UUID routeId
    ) {
        deliveryCommandService.departRoute(deliveryId, routeId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deliveries/{deliveryId}/routes/{routeId}/arrival")
    public ResponseEntity<ApiResponse<Void>> arriveDeliveryRoute(
            @PathVariable("deliveryId") UUID deliveryId,
            @PathVariable("routeId") UUID routeId
    ) {
        deliveryCommandService.arriveRoute(deliveryId, routeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deliveries/{deliveryId}/company-departure")
    public ResponseEntity<ApiResponse<Void>> departDeliveryCompany(@PathVariable("deliveryId") UUID deliveryId) {
        deliveryCommandService.startCompanyDelivery(deliveryId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deliveries/{deliveryId}/company-completion")
    public ResponseEntity<ApiResponse<Void>> completeDeliveryCompany(@PathVariable("deliveryId") UUID deliveryId) {

        deliveryCommandService.completeDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }

}
