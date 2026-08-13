package com.iftrue.delivery.application.service.deliverymanager;

import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerRepository;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerType;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import com.iftrue.delivery.infrastructure.redis.DeliveryManagerRoundRobinCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerAssignmentService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryManagerRoundRobinCounter deliveryManagerRoundRobinCounter;

    @Transactional
    public DeliveryManager nextHubManager() {

        List<DeliveryManager> deliveryManagers = deliveryManagerRepository
                .findAllByTypeAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType.HUB);

        if (deliveryManagers.isEmpty()) {
            throw new DeliveryServiceException(ErrorCode.DELIVERY_MANAGER_NOT_FOUND);
        }

        long counter = deliveryManagerRoundRobinCounter.nextHub();

        int index = (int) ((counter - 1) % deliveryManagers.size());

        return deliveryManagers.get(index);
    }

    @Transactional
    public DeliveryManager nextCompanyManager(UUID destinationHubId) {
        List<DeliveryManager> deliveryManagers = deliveryManagerRepository
                .findAllByTypeAndHubIdAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType.COMPANY, destinationHubId);

        if (deliveryManagers.isEmpty()) {
            throw new DeliveryServiceException(ErrorCode.DELIVERY_MANAGER_NOT_FOUND);
        }

        long counter = deliveryManagerRoundRobinCounter.nextCompany(destinationHubId);

        int index = (int) ((counter - 1) % deliveryManagers.size());

        return deliveryManagers.get(index);
    }
}
