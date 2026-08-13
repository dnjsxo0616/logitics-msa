package com.iftrue.delivery.infrastructure.persistence.deliveryroute;

import com.iftrue.delivery.domain.deliveryroute.DeliveryRoute;
import com.iftrue.delivery.domain.deliveryroute.DeliveryRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRouteRepositoryAdapter implements DeliveryRouteRepository {
    private final JpaDeliveryRouteRepository jpaDeliveryRouteRepository;

    @Override
    public DeliveryRoute save(DeliveryRoute deliveryRoute) {
        return jpaDeliveryRouteRepository.save(deliveryRoute);
    }

    @Override
    public Optional<DeliveryRoute> findById(UUID deliveryRouteId) {
        return jpaDeliveryRouteRepository.findById(deliveryRouteId);
    }
}
