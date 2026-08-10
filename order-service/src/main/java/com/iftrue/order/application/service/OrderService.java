package com.iftrue.order.application.service;

import com.iftrue.order.domain.Order;
import com.iftrue.order.domain.OrderRepository;
import com.iftrue.order.global.exception.BusinessException;
import com.iftrue.order.global.exception.OrderErrorCode;
import com.iftrue.order.global.security.AuthenticatedUser;
import com.iftrue.order.presentation.dto.OrderCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String MASTER_ROLE = "MASTER";

    private final OrderRepository orderRepository;

    @Transactional
    public void createOrder(OrderCreateRequest request, AuthenticatedUser user) {
        validateCompanyScope(request, user);

        Order newOrder = Order.create(request.receiverCompanyId(),
                request.supplierCompanyId(),
                request.productId(),
                request.quantity(),
                request.requestMessage());

        orderRepository.save(newOrder);
    }

    private void validateCompanyScope(OrderCreateRequest request, AuthenticatedUser user) {
        if (MASTER_ROLE.equals(user.role())) {
            return;
        }

        if (user.companyId() == null || !user.companyId().equals(request.receiverCompanyId())) {
            throw new BusinessException(OrderErrorCode.ACCESS_DENIED);
        }
    }
}
