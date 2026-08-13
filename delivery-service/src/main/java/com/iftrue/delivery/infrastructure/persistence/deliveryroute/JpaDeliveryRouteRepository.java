package com.iftrue.delivery.infrastructure.persistence.deliveryroute;

import com.iftrue.delivery.domain.deliveryroute.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaDeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID> {
}
