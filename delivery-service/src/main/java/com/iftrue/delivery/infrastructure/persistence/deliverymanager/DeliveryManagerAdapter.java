package com.iftrue.delivery.infrastructure.persistence.deliverymanager;

import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerRepository;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryManagerAdapter implements DeliveryManagerRepository {

    private final JpaDeliveryManagerRepository jpaDeliveryManagerRepository;


    @Override
    public DeliveryManager save(DeliveryManager deliveryManager) {
        return jpaDeliveryManagerRepository.save(deliveryManager);
    }

    @Override
    public Optional<DeliveryManager> findById(UUID deliveryManagerId) {
        return jpaDeliveryManagerRepository.findById(deliveryManagerId);
    }

    @Override
    public List<DeliveryManager> findAllByTypeAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType deliveryManagerType) {
        return jpaDeliveryManagerRepository.findAllByTypeAndDeletedAtIsNullOrderBySequenceAsc(deliveryManagerType);
    }

    @Override
    public List<DeliveryManager> findAllByTypeAndHubIdAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType deliveryManagerType, UUID destinationHubId) {
        return jpaDeliveryManagerRepository.findAllByTypeAndHubIdAndDeletedAtIsNullOrderBySequenceAsc(deliveryManagerType, destinationHubId);
    }

    @Override
    public long countByTypeAndDeletedAtIsNull(DeliveryManagerType deliveryManagerType) {
        return jpaDeliveryManagerRepository.countByTypeAndDeletedAtIsNull(deliveryManagerType);
    }

    @Override
    public Optional<DeliveryManager> findTopByTypeOrderBySequenceDesc(DeliveryManagerType deliveryManagerType) {
        return jpaDeliveryManagerRepository.findTopByTypeOrderBySequenceDesc(deliveryManagerType);
    }

    @Override
    public long countByTypeAndHubIdAndDeletedAtIsNull(DeliveryManagerType deliveryManagerType, UUID hubId) {
        return jpaDeliveryManagerRepository.countByTypeAndHubIdAndDeletedAtIsNull(deliveryManagerType, hubId);
    }

    @Override
    public Optional<DeliveryManager> findTopByTypeAndHubIdOrderBySequenceDesc(DeliveryManagerType deliveryManagerType, UUID hubId) {
        return jpaDeliveryManagerRepository.findTopByTypeAndHubIdOrderBySequenceDesc(deliveryManagerType, hubId);
    }

    @Override
    public boolean existsById(UUID deliveryUserId) {
        return jpaDeliveryManagerRepository.existsById(deliveryUserId);
    }
}
