package com.iftrue.notification.domain.slackmessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, UUID> {

    boolean existsByAiAlert_Id(UUID aiAlertId);

    @Query(value = """
            SELECT *
            FROM notification_schema.p_slack_message
            WHERE deleted_at IS NULL
              AND status = 'SENDING'
              AND updated_at <= :timeoutThreshold
            ORDER BY updated_at
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    Optional<SlackMessage> findNextTimedOutSending(
            @Param("timeoutThreshold") Instant timeoutThreshold
    );
}
