package com.iftrue.delivery.application.service.deliverymanager;

import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerRepository;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerType;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerAssignmentService {

    private final DeliveryManagerRepository deliveryManagerRepository;

    @Transactional
    public DeliveryManager nextHubManager() {
        // TODO: 동시성 이슈 해결 필요, 현재 단계 기본 구현 빠르게
        List<DeliveryManager> deliveryManagers = deliveryManagerRepository
                .findAllByTypeAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType.HUB);

        if (deliveryManagers.isEmpty()) {
            throw new DeliveryServiceException(ErrorCode.DELIVERY_MANAGER_NOT_FOUND);
        }

        DeliveryManager lastAssignedDeliveryManager = deliveryManagers.stream()
                .filter(deliveryManager -> deliveryManager.getLastAssignedAt() != null)
                .max(Comparator.comparing(
                        DeliveryManager::getLastAssignedAt
                ))
                .orElse(null);

        DeliveryManager nextManager;


        if (lastAssignedDeliveryManager == null) {
            nextManager = deliveryManagers.get(0);
        } else {
            int currentIndex = deliveryManagers.indexOf(lastAssignedDeliveryManager);
            int nextIndex = (currentIndex + 1) % deliveryManagers.size();
            nextManager = deliveryManagers.get(nextIndex);
        }

        nextManager.markAssignedAt();

        return nextManager;
    }

    @Transactional
    public DeliveryManager nextCompanyManager(UUID destinationHubId) {
        // TODO: 동시성 이슈 해결 필요, 현재 단계 기본 구현 빠르게
        List<DeliveryManager> deliveryManagers = deliveryManagerRepository
                .findAllByTypeAndHubIdAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType.COMPANY, destinationHubId);

        if (deliveryManagers.isEmpty()) {
            throw new DeliveryServiceException(ErrorCode.DELIVERY_MANAGER_NOT_FOUND);
        }

        DeliveryManager lastAssignedDeliveryManager =
                deliveryManagers.stream()
                        .filter(manager -> manager.getLastAssignedAt() != null)
                        .max(Comparator.comparing(
                                DeliveryManager::getLastAssignedAt
                        ))
                        .orElse(null);

        DeliveryManager nextManager;
        if (lastAssignedDeliveryManager == null) {
            nextManager = deliveryManagers.get(0);
        } else {
            int currentIndex =
                    deliveryManagers.indexOf(lastAssignedDeliveryManager);
            int nextIndex =
                    (currentIndex + 1) % deliveryManagers.size();
            nextManager = deliveryManagers.get(nextIndex);
        }

        nextManager.markAssignedAt();
        return nextManager;
    }
}
