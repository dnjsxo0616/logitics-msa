package com.iftrue.notification.domain.aialert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AiAlertRepository extends JpaRepository<AiAlert, UUID> {

    Optional<AiAlert> findByDeliveryId(UUID deliveryId);

    @Query(value = """
            SELECT *
            FROM notification_schema.p_ai_alert
            WHERE id = :aiAlertId
              AND deleted_at IS NULL
              AND status = 'COMPLETED'
            FOR UPDATE
            """, nativeQuery = true)
    Optional<AiAlert> findCompletedByIdForUpdate(
            @Param("aiAlertId") UUID aiAlertId
    );

    @Query(value = """
            SELECT ai_alert.*
            FROM notification_schema.p_ai_alert ai_alert
            WHERE ai_alert.deleted_at IS NULL
              AND ai_alert.status = 'COMPLETED'
              AND NOT EXISTS (
                  SELECT 1
                  FROM notification_schema.p_slack_message slack_message
                  WHERE slack_message.ai_alert_id = ai_alert.id
              )
            ORDER BY ai_alert.created_at
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    Optional<AiAlert> findNextCompletedForSlackMessage();

    @Query(value = """
            SELECT *
            FROM notification_schema.p_ai_alert
            WHERE deleted_at IS NULL
              AND status = 'PENDING'
            ORDER BY created_at
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    Optional<AiAlert> findNextForProcessing();

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
