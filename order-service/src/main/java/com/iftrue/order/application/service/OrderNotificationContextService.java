package com.iftrue.order.application.service;

import com.iftrue.order.domain.Order;
import com.iftrue.order.domain.OrderRepository;
import com.iftrue.order.global.exception.BusinessException;
import com.iftrue.order.global.exception.OrderErrorCode;
import com.iftrue.order.infrastructure.client.product.dto.ProductResponse;
import com.iftrue.order.infrastructure.client.user.dto.UserResponse;
import com.iftrue.order.presentation.dto.OrderNotificationContextResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderNotificationContextService {

    private final OrderRepository orderRepository;
    private final OrderExternalService orderExternalService;

    public OrderNotificationContextResponse getNotificationContext(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        UserResponse requester = orderExternalService.getRecipient(order.getCreatedBy());
        ProductResponse product = orderExternalService.getProduct(order.getProductId());

        return new OrderNotificationContextResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getRequestedArrivalAt(),
                order.getCreatedBy(),
                order.getReceiverCompanyId(),
                order.getSupplierCompanyId(),
                requester.name(),
                requester.email(),
                product.productName(),
                order.getQuantity(),
                order.getRequestMessage(),
                order.getStatus()
        );
    }
}
