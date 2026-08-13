package com.iftrue.delivery.application.service.delivery;

import com.iftrue.delivery.application.service.deliverymanager.DeliveryManagerAssignmentService;
import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryRepository;
import com.iftrue.delivery.domain.delivery.DeliveryStatus;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryCommandService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryManagerAssignmentService deliveryManagerAssignmentService;

    @Transactional
    public void cancel(UUID deliveryId) {

        Delivery delivery = getDelivery(deliveryId);

        delivery.cancel();
    }

    @Transactional
    public void departRoute(
            UUID deliveryId,
            UUID routeId
    ) {
        Delivery delivery = getDelivery(deliveryId);
        Instant departedAt = Instant.now();
        delivery.startRoute(routeId, departedAt);
    }

    @Transactional
    public void arriveRoute(
            UUID deliveryId,
            UUID routeId

    ) {
        Delivery delivery = getDelivery(deliveryId);
        Instant arrivedAt = Instant.now();
        delivery.arriveRoute(routeId, arrivedAt);

        if (delivery.getStatus() == DeliveryStatus.ARRIVED_AT_DESTINATION_HUB) {
            DeliveryManager manager =
                    deliveryManagerAssignmentService.nextCompanyManager(delivery.getDestinationHubId());
            delivery.assignCompanyManager(manager);
        }
    }

    public void startCompanyDelivery(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.startCompanyDelivery();
    }

    public void completeDelivery(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.completeDelivery();
    }

    private Delivery getDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() ->
                        new DeliveryServiceException(
                                ErrorCode.DELIVERY_NOT_FOUND
                        )
                );
    }

}
