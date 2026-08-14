package com.iftrue.delivery.infrastructure.persistence.deliveryroute;

import com.iftrue.delivery.application.dto.deliveryroute.DeliveryRouteResult;
import com.iftrue.delivery.application.repository.DeliveryRouteQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRouteQueryRepositoryAdapter implements DeliveryRouteQueryRepository {
    private final JpaDeliveryRouteRepository jpaDeliveryRouteRepository;

    @Override
    public List<DeliveryRouteResult> findAllByDeliveryId(UUID deliveryId) {
        return jpaDeliveryRouteRepository.findAllRouteResultsByDeliveryId(deliveryId);
    }

    @Override
    public Optional<DeliveryRouteResult> findById(UUID deliveryRouteId) {
        return jpaDeliveryRouteRepository.findById(deliveryRouteId).map(DeliveryRouteResult::from);
    }

}
