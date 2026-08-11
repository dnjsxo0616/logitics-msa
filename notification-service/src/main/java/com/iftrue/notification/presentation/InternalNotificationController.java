package com.iftrue.notification.presentation;

import com.iftrue.notification.application.service.AiAlertCommandService;
import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.global.response.ApiResponse;
import com.iftrue.notification.presentation.dto.AiAlertResponse;
import com.iftrue.notification.presentation.dto.DeliveryCreatedRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/notifications")
public class InternalNotificationController {

    private final AiAlertCommandService aiAlertCommandService;

    @PostMapping("/delivery-created")
    public ResponseEntity<ApiResponse<AiAlertResponse>> deliveryCreated(
            @RequestBody @Valid DeliveryCreatedRequest request
    ) {
        AiAlert aiAlert = aiAlertCommandService.create(request);
        AiAlertResponse response = AiAlertResponse.from(aiAlert);

        return ResponseEntity.accepted()
                .body(ApiResponse.accepted(response));
    }
}
