package com.iftrue.delivery.infrastructure.persistence.delivery;

import com.iftrue.delivery.domain.delivery.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaDeliveryRepository extends JpaRepository<Delivery, UUID> {
    boolean existsByOrderId(UUID orderId);
}
