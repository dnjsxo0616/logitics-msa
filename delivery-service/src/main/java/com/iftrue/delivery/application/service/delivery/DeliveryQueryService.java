package com.iftrue.delivery.application.service.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryResult;
import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryRepository;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryQueryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryResult getDeliveryByOrderId(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new DeliveryServiceException(ErrorCode.DELIVERY_NOT_FOUND)
                );

        return DeliveryResult.from(delivery);
    }
}
