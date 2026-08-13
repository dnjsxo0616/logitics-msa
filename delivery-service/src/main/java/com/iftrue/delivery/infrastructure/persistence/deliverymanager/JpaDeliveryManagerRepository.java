package com.iftrue.delivery.infrastructure.persistence.deliverymanager;

import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDeliveryManagerRepository extends JpaRepository<DeliveryManager, UUID> {

    List<DeliveryManager> findAllByTypeAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType deliveryManagerType);

    List<DeliveryManager> findAllByTypeAndHubIdAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType deliveryManagerType, UUID destinationHubId);

    long countByTypeAndDeletedAtIsNull(DeliveryManagerType deliveryManagerType);

    long countByTypeAndHubIdAndDeletedAtIsNull(DeliveryManagerType deliveryManagerType, UUID hubId);

    Optional<DeliveryManager> findTopByTypeOrderBySequenceDesc(DeliveryManagerType deliveryManagerType);

    Optional<DeliveryManager> findTopByTypeAndHubIdOrderBySequenceDesc(DeliveryManagerType deliveryManagerType, UUID hubId);
}
