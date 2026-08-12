package com.iftrue.delivery.presentation;

import com.iftrue.delivery.application.service.deliverymanager.DeliveryManagerCreateService;
import com.iftrue.delivery.global.common.ApiResponse;
import com.iftrue.delivery.presentation.dto.deliverymanager.DeliveryManagerCreateRequest;
import com.iftrue.delivery.presentation.dto.deliverymanager.DeliveryManagerCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/internal/delivery-managers")
public class InternalDeliveryManagerController {

    private final DeliveryManagerCreateService deliveryManagerCreateService;

    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryManagerCreateResponse>> createDeliveryManager(
            @Valid @RequestBody DeliveryManagerCreateRequest request
    ) {
        UUID managerId = deliveryManagerCreateService.create(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                                HttpStatus.CREATED.value(),
                                new DeliveryManagerCreateResponse(managerId)
                        )
                );
    }

}
