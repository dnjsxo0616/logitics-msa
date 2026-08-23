package com.iftrue.notification.domain.aialert;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AiAlertRepository extends JpaRepository<AiAlert, UUID> {

    Optional<AiAlert> findByDeliveryId(UUID deliveryId);
}
