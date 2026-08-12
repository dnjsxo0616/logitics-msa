package com.iftrue.delivery.domain.deliverymanager;

import com.iftrue.delivery.domain.common.DeletableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_delivery_manager")
public class DeliveryManager extends DeletableEntity {

    @Id
    private UUID id;

    @Column(name = "slack_id", nullable = false, length = 50)
    private String slackId;

    @Column(name = "hub_id")
    private UUID hubId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private DeliveryManagerType type;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "last_assigned_at")
    private Instant lastAssignedAt;

    private DeliveryManager(
            UUID id,
            String slackId,
            UUID hubId,
            DeliveryManagerType type,
            int sequence
    ) {
        this.id = Objects.requireNonNull(id, "사용자 ID는 필수입니다.");
        this.slackId = requireText(slackId, "Slack ID는 필수입니다.");
        this.type = Objects.requireNonNull(type, "배송담당자 유형은 필수입니다.");

        validateHubId(type, hubId);
        validateSequence(sequence);

        this.hubId = hubId;
        this.sequence = sequence;
    }

    public static DeliveryManager createHubManager(
            UUID userId,
            String slackId,
            int sequence
    ) {

        return new DeliveryManager(
                userId,
                slackId,
                null,
                DeliveryManagerType.HUB,
                sequence
        );
    }

    public static DeliveryManager createCompanyManager(
            UUID userId,
            String slackId,
            UUID hubId,
            int sequence
    ) {
        Objects.requireNonNull(hubId, "업체 배송담당자는 소속 허브가 필수 입니다.");

        return new DeliveryManager(
                userId,
                slackId,
                hubId,
                DeliveryManagerType.COMPANY,
                sequence
        );
    }

    public void markAssignedAt() {
        this.lastAssignedAt = Instant.now();
    }

    // 업체 배송 담당자 검증
    public void validateCompanyDeliveryAssignable(UUID destinationHubId) {
        Objects.requireNonNull(destinationHubId, "목적지 허브 ID는 필수입니다.");

        if (type != DeliveryManagerType.COMPANY) {
            throw new IllegalStateException("업체 배송담당자만 업체 배송을 담당할 수 있습니다.");
        }

        if (isDeleted()) {
            throw new IllegalStateException("삭제된 배송담당자는 배정할 수 없습니다.");
        }

        if (!Objects.equals(hubId, destinationHubId)) {
            throw new IllegalStateException("목적지 허브 소속 배송담당자가 아닙니다.");
        }
    }

    // 허브 배송 담당자 검증
    public void validateHubDeliveryAssignable() {
        if (type != DeliveryManagerType.HUB) {
            throw new IllegalStateException("허브 배송담당자만 허브 간 배송을 담당할 수 있습니다.");
        }
        if (isDeleted()) {
            throw new IllegalStateException("삭제된 배송담당자는 배정할 수 없습니다.");
        }
    }

    // 검증
    private static void validateHubId(
            DeliveryManagerType type,
            UUID hubId
    ) {
        if (type == DeliveryManagerType.COMPANY && hubId == null) {
            throw new IllegalArgumentException("업체 배송담당자는 허브 ID가 필수입니다.");
        }

        if (type == DeliveryManagerType.HUB && hubId != null) {
            throw new IllegalArgumentException("허브 배송담당자는 허브 ID를 가질 수 없습니다.");
        }
    }

    private static void validateSequence(int sequence) {
        if (sequence < 1) {
            throw new IllegalArgumentException("배송담당자 순번은 1 이상이어야 합니다.");
        }
    }

    // 공통 값 검증
    private static String requireText(
            String value,
            String message
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }


}
