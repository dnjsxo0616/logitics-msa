package com.iftrue.delivery.infrastructure.persistence.deliveryroute;

import com.iftrue.delivery.application.dto.deliveryroute.DeliveryRouteResult;
import com.iftrue.delivery.domain.deliveryroute.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface JpaDeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID> {
    @Query("""
                select new com.iftrue.delivery.application.dto.deliveryroute.DeliveryRouteResult(
                    r.id,
                    r.departureHubId,
                    r.arrivalHubId,
                    r.hubDeliveryManagerId,
                    r.sequence,
                    r.status,
                    r.expectedDistance,
                    r.expectedDuration
                )
                from DeliveryRoute r
                where r.delivery.id = :deliveryId
                order by r.sequence asc
            """)
    List<DeliveryRouteResult> findAllRouteResultsByDeliveryId(UUID deliveryId);
}
