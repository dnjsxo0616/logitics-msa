package com.iftrue.notification.domain.slackmessage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, UUID> {

    boolean existsByAiAlert_Id(UUID aiAlertId);
}
