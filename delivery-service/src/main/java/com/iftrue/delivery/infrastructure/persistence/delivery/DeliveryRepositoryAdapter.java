package com.iftrue.delivery.infrastructure.persistence.delivery;

import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryAdapter implements DeliveryRepository {

    private final JpaDeliveryRepository jpaDeliveryRepository;

    @Override
    public Delivery save(Delivery delivery) {
        return jpaDeliveryRepository.save(delivery);
    }

    @Override
    public Optional<Delivery> findById(UUID deliveryId) {
        return jpaDeliveryRepository.findById(deliveryId);
    }

    @Override
    public boolean existsByOrderId(UUID orderId) {
        return jpaDeliveryRepository.existsByOrderId(orderId);
    }
}
