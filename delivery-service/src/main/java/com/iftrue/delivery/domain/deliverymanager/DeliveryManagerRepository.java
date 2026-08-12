package com.iftrue.delivery.domain.deliverymanager;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {

    DeliveryManager save(DeliveryManager deliveryManager);

    Optional<DeliveryManager> findById(UUID deliveryManagerId);

    List<DeliveryManager> findAllByTypeAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType deliveryManagerType);

    List<DeliveryManager> findAllByTypeAndHubIdAndDeletedAtIsNullOrderBySequenceAsc(DeliveryManagerType deliveryManagerType, UUID destinationHubId);

    long countByTypeAndDeletedAtIsNull(DeliveryManagerType deliveryManagerType);

    Optional<DeliveryManager> findTopByTypeOrderBySequenceDesc(DeliveryManagerType deliveryManagerType);

    long countByTypeAndHubIdAndDeletedAtIsNull(DeliveryManagerType deliveryManagerType, UUID hubId);

    Optional<DeliveryManager> findTopByTypeAndHubIdOrderBySequenceDesc(DeliveryManagerType deliveryManagerType, UUID hubId);

    boolean existsById(UUID deliveryUserId);
}
