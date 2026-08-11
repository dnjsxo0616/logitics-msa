package com.iftrue.delivery.application.service.delivery;

import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryRepository;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryCommandService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public void cancel(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() ->
                new DeliveryServiceException(ErrorCode.DELIVERY_NOT_FOUND)
        );

        delivery.cancel();
    }
}
