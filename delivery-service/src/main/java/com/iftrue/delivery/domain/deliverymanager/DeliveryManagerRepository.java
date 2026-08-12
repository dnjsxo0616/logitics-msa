package com.iftrue.delivery.domain.deliverymanager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {

    DeliveryManager save(DeliveryManager deliveryManager);

    Optional<DeliveryManager> findById(UUID deliveryManagerId);

    List<DeliveryManager> findAllByTypeAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType deliveryManagerType);

    List<DeliveryManager> findAllByTypeAndHubIdAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType deliveryManagerType, UUID destinationHubId);
}
