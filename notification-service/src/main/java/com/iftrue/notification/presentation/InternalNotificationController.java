package com.iftrue.notification.presentation;

import com.iftrue.notification.application.dto.NotificationCreateResult;
import com.iftrue.notification.application.service.NotificationCreateService;
import com.iftrue.notification.global.response.ApiResponse;
import com.iftrue.notification.presentation.request.NotificationCreateRequest;
import com.iftrue.notification.presentation.response.NotificationCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/notifications")
public class InternalNotificationController {

    private final NotificationCreateService notificationCreateService;

    @PostMapping("/deliveries")
    public ResponseEntity<ApiResponse<NotificationCreateResponse>> create(
            @Valid @RequestBody NotificationCreateRequest request
    ) {
        NotificationCreateResult result =
                notificationCreateService.create(
                        request.orderId(),
                        request.deliveryId(),
                        request.toAiRequestPayload()
                );
        NotificationCreateResponse response =
                NotificationCreateResponse.from(result.aiAlert());

        if (!result.completed()) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(ApiResponse.accepted(response));
        }

        if (result.created()) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.created(response));
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
