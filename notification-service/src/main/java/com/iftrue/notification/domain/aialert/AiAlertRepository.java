package com.iftrue.notification.domain.aialert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AiAlertRepository extends JpaRepository<AiAlert, UUID> {

    boolean existsByDeliveryId(UUID deliveryId);

    Optional<AiAlert> findByDeliveryId(UUID deliveryId);

    @Query(value = """
            SELECT *
            FROM notification_schema.p_ai_alert
            WHERE deleted_at IS NULL
              AND (
                    status = 'PENDING'
                    OR (status = 'RETRY_WAIT' AND next_retry_at <= :now)
                  )
            ORDER BY created_at
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    Optional<AiAlert> findNextForProcessing(@Param("now") Instant now);

    @Query(value = """
            SELECT *
            FROM notification_schema.p_ai_alert
            WHERE deleted_at IS NULL
              AND status = 'PROCESSING'
              AND updated_at <= :timeoutThreshold
            ORDER BY updated_at
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    Optional<AiAlert> findNextTimedOutProcessing(
            @Param("timeoutThreshold") Instant timeoutThreshold
    );
}
