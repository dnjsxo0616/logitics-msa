package com.iftrue.notification.application.service;

import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertRepository;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import com.iftrue.notification.global.response.ApiResponse;
import com.iftrue.notification.infrastructure.client.order.OrderClient;
import com.iftrue.notification.infrastructure.client.order.dto.OrderNotificationContext;
import com.iftrue.notification.infrastructure.client.order.dto.OrderStatus;
import com.iftrue.notification.presentation.dto.DeliveryCreatedRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiAlertCommandService {

    private final AiAlertRepository aiAlertRepository;
    private final OrderClient orderClient;

    @Transactional
    public AiAlert create(DeliveryCreatedRequest request) {
        return aiAlertRepository.findByDeliveryId(request.deliveryId())
                .orElseGet(() -> createNewAlert(request));
    }

    private AiAlert createNewAlert(DeliveryCreatedRequest request) {
        OrderNotificationContext orderContext = getOrderContext(request.orderId());

        validateOrderContext(request.orderId(), orderContext);

        AiAlert aiAlert = AiAlert.create(
                orderContext.orderId(),
                request.deliveryId(),
                request.toDeliveryPayload()
        );

        return aiAlertRepository.save(aiAlert);
    }

    private OrderNotificationContext getOrderContext(UUID orderId) {
        try {
            ApiResponse<OrderNotificationContext> response = orderClient.getNotificationContext(orderId);

            if (response == null || response.getData() == null) {
                throw new BusinessException(NotificationErrorCode.ORDER_SERVICE_CALL_FAILED);
            }

            return response.getData();
        } catch (FeignException exception) {
            throw new BusinessException(NotificationErrorCode.ORDER_SERVICE_CALL_FAILED);
        }
    }

    private void validateOrderContext(UUID requestedOrderId, OrderNotificationContext orderContext) {
        if (!Objects.equals(requestedOrderId, orderContext.orderId())) {
            throw new BusinessException(NotificationErrorCode.ORDER_SERVICE_CALL_FAILED);
        }

        if (orderContext.status() != OrderStatus.PENDING && orderContext.status() != OrderStatus.CONFIRMED) {
            throw new BusinessException(NotificationErrorCode.ORDER_STATUS_NOT_ALLOWED);
        }
    }
}
