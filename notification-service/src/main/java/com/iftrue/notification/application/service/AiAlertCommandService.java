package com.iftrue.notification.application.service;

import com.iftrue.notification.domain.aialert.AiAlert;
import com.iftrue.notification.domain.aialert.AiAlertRepository;
import com.iftrue.notification.domain.aialert.AiAlertStatus;
import com.iftrue.notification.global.exception.BusinessException;
import com.iftrue.notification.global.exception.NotificationErrorCode;
import com.iftrue.notification.global.response.ApiResponse;
import com.iftrue.notification.infrastructure.client.order.OrderClient;
import com.iftrue.notification.infrastructure.client.order.dto.OrderNotificationContext;
import com.iftrue.notification.infrastructure.client.order.dto.OrderStatus;
import com.iftrue.notification.presentation.dto.DeliveryCreatedRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiAlertCommandService {

    private final AiAlertRepository aiAlertRepository;
    private final OrderClient orderClient;

    public AiAlert create(DeliveryCreatedRequest request) {
        return aiAlertRepository.findByDeliveryId(request.deliveryId())
                .orElseGet(() -> createNewAlert(request));
    }

    @Transactional
    public AiAlert cancel(UUID deliveryId) {
        AiAlert aiAlert = aiAlertRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new BusinessException(
                        NotificationErrorCode.NOTIFICATION_NOT_FOUND
                ));

        if (aiAlert.getStatus() == AiAlertStatus.CANCELED) {
            return aiAlert;
        }

        aiAlert.cancel();
        return aiAlert;
    }

    private AiAlert createNewAlert(DeliveryCreatedRequest request) {
        OrderNotificationContext orderContext = getOrderContext(request.orderId());

        validateOrderContext(request.orderId(), orderContext);

        AiAlert aiAlert = AiAlert.create(
                orderContext.orderId(),
                request.deliveryId(),
                orderContext.toOrderPayload(),
                request.toDeliveryPayload()
        );

        return saveOrFindExisting(aiAlert);
    }

    private AiAlert saveOrFindExisting(AiAlert aiAlert) {
        try {
            return aiAlertRepository.saveAndFlush(aiAlert);
        } catch (DataIntegrityViolationException exception) {
            return aiAlertRepository.findByDeliveryId(aiAlert.getDeliveryId())
                    .orElseThrow(() -> exception);
        }
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
