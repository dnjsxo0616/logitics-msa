package com.iftrue.delivery.domain.deliveryroute;

import com.iftrue.delivery.domain.delivery.Delivery;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRouteRepository {

    DeliveryRoute save(DeliveryRoute deliveryRoute);

    Optional<DeliveryRoute> findById(UUID deliveryRouteId);
}
