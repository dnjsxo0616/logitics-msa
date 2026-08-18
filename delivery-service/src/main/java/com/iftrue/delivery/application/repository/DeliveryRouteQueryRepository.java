package com.iftrue.delivery.application.repository;

import com.iftrue.delivery.application.dto.deliveryroute.DeliveryRouteResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRouteQueryRepository {

    List<DeliveryRouteResult> findAllByDeliveryId(UUID deliveryId);

    Optional<DeliveryRouteResult> findById(UUID deliveryRouteId);
}
