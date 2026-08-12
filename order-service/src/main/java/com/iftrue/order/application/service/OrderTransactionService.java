package com.iftrue.order.application.service;

import com.iftrue.order.domain.Order;
import com.iftrue.order.domain.OrderRepository;
import com.iftrue.order.global.exception.BusinessException;
import com.iftrue.order.global.exception.OrderErrorCode;
import com.iftrue.order.presentation.dto.OrderCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderTransactionService {

    private final OrderRepository orderRepository;
    private final RequestedArrivalTimeValidator requestedArrivalTimeValidator;

    @Transactional
    public UUID createPendingOrder(OrderCreateRequest request) {
        requestedArrivalTimeValidator.validate(request.requestedArrivalAt());

        Order order = Order.create(
                request.receiverCompanyId(),
                request.supplierCompanyId(),
                request.productId(),
                request.quantity(),
                request.requestMessage(),
                request.requestedArrivalAt()
        );

        return orderRepository.save(order).getId();
    }

    @Transactional
    public void confirmOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.confirm();
    }

    @Transactional
    public void failOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.fail();
    }

    @Transactional
    public void completeOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.complete();
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
    }
}
