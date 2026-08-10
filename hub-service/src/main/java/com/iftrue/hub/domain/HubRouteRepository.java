package com.iftrue.hub.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {

    boolean existsByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(
            UUID departureHubId, UUID arrivalHubId
    );

    Optional<HubRoute> findByIdAndDeletedAtIsNull(UUID id);

    Page<HubRoute> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("""
                SELECT hr FROM HubRoute hr
                WHERE hr.deletedAt IS NULL
                AND (:departureHubId IS NULL OR hr.departureHubId = :departureHubId)
                AND (:arrivalHubId   IS NULL OR hr.arrivalHubId   = :arrivalHubId)
            """)
    Page<HubRoute> search(@Param("departureHubId") UUID departureHubId,
                          @Param("arrivalHubId") UUID arrivalHubId,
                          Pageable pageable
    );

    @Query("""
                SELECT hr FROM HubRoute hr
                WHERE hr.deletedAt IS NULL
                AND (hr.departureHubId = :hubId OR hr.arrivalHubId = :hubId)
            """)
    List<HubRoute> findActiveRoutesByHubId(@Param("hubId") UUID hubId);
}
