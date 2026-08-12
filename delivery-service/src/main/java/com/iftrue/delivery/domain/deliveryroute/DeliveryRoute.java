package com.iftrue.delivery.domain.deliveryroute;

import com.iftrue.delivery.domain.common.DeletableEntity;
import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_delivery_route")
public class DeliveryRoute extends DeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(name = "hub_delivery_manager_id")
    private UUID hubDeliveryManagerId;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private HubDeliveryStatus status;

    @Column(name = "departed_at")
    private Instant departedAt;

    @Column(name = "arrived_at")
    private Instant arrivedAt;

    @Column(name = "expected_distance", nullable = false, precision = 10, scale = 2)
    private BigDecimal expectedDistance;

    @Column(name = "expected_duration", nullable = false)
    private int expectedDuration;

    @Column(name = "actual_distance", precision = 10, scale = 2)
    private BigDecimal actualDistance;

    @Column(name = "actual_duration")
    private Integer actualDuration;

    private DeliveryRoute(
            Delivery delivery,
            UUID departureHubId,
            UUID arrivalHubId,
            int sequence,
            BigDecimal expectedDistance,
            Integer expectedDuration

    ) {
        this.delivery = Objects.requireNonNull(delivery, "배송은 필수입니다.");
        this.departureHubId = Objects.requireNonNull(departureHubId, "출발 허브 ID는 필수입니다.");
        this.arrivalHubId = Objects.requireNonNull(arrivalHubId, "도착 허브 ID는 필수입니다.");

        validateSequence(sequence);
        validateExpectedDistance(expectedDistance);
        validateExpectedDuration(expectedDuration);

        this.sequence = sequence;
        this.status = HubDeliveryStatus.WAITING_FOR_DEPARTURE;
        this.expectedDistance = expectedDistance;
        this.expectedDuration = expectedDuration;

    }

    public static DeliveryRoute create(
            Delivery delivery,
            UUID departureHubId,
            UUID arrivalHubId,
            int sequence,
            BigDecimal expectedDistance,
            int expectedDuration
    ) {
        return new DeliveryRoute(
                delivery,
                departureHubId,
                arrivalHubId,
                sequence,
                expectedDistance,
                expectedDuration
        );
    }

    // 허브 배송 담당자 배정
    public void assignHubDeliveryManager(DeliveryManager manager) {
        Objects.requireNonNull(manager, "배송담당자는 필수입니다.");

        if (status != HubDeliveryStatus.WAITING_FOR_DEPARTURE) {
            throw new IllegalStateException("출발 대기 상태의 경로에만 담당자를 배정할 수 있습니다.");
        }

        manager.validateHubDeliveryAssignable();

        // 현재 정책: 출발 전에는 담당자 재배정을 허용한다.
        this.hubDeliveryManagerId = manager.getId();
    }

    // 허브 배송 상태 전이, 배송 출발
    public void depart(Instant departedAt) {
        if (status != HubDeliveryStatus.WAITING_FOR_DEPARTURE) {
            throw new IllegalStateException("출발 대기 상태의 배송 경로만 출발할 수 있습니다.");
        }

        if (hubDeliveryManagerId == null) {
            throw new IllegalStateException("허브 배송담당자가 배정되지 않았습니다.");
        }

        this.departedAt = Objects.requireNonNull(departedAt, "출발 시각은 필수입니다.");
        this.status = HubDeliveryStatus.IN_TRANSIT;

    }

    public void arrive(
            Instant arrivedAt
    ) {
        if (status != HubDeliveryStatus.IN_TRANSIT) {
            throw new IllegalStateException("이동 중인 배송 경로만 도착 처리할 수 있습니다.");
        }

        Instant validatedArrivedAt = Objects.requireNonNull(arrivedAt, "도착 시각은 필수입니다.");

        if (departedAt == null) {
            throw new IllegalStateException("출발 시각이 기록되지 않았습니다.");
        }

        if (validatedArrivedAt.isBefore(departedAt)) {
            throw new IllegalArgumentException("도착 시각은 출발 시각보다 빠를 수 없습니다.");
        }

        long durationMinutes =
                Duration.between(
                        departedAt,
                        validatedArrivedAt
                ).toMinutes();

        BigDecimal calculatedActualDistance = this.expectedDistance;

        int calculatedActualDuration =
                Math.toIntExact(durationMinutes);

        validateActualDistance(calculatedActualDistance);
        validateActualDuration(calculatedActualDuration);

        this.arrivedAt = validatedArrivedAt;
        this.actualDistance = calculatedActualDistance;
        this.actualDuration = calculatedActualDuration;
        this.status = HubDeliveryStatus.ARRIVED;
    }

    // 조회성 도메인 메서드
    public boolean isArrived() {
        return status == HubDeliveryStatus.ARRIVED;
    }

    public boolean hasId(UUID routeId) {
        return Objects.equals(this.id, routeId);
    }


    public boolean hasSequence(int sequence) {
        return this.sequence == sequence;
    }

    public boolean isFirstRoute() {
        return sequence == 1;
    }

    // 생성값 검증
    private static void validateSequence(int sequence) {
        if (sequence < 1) {
            throw new IllegalArgumentException("배송 경로 순번은 1 이상이어야 합니다.");
        }
    }

    private static void validateExpectedDistance(
            BigDecimal expectedDistance
    ) {
        if (expectedDistance == null
                || expectedDistance.signum() < 0) {
            throw new IllegalArgumentException("예상 거리는 0 이상이어야 합니다.");
        }
    }

    private static void validateExpectedDuration(
            int expectedDuration
    ) {
        if (expectedDuration < 0) {
            throw new IllegalArgumentException("예상 소요 시간은 0 이상이어야 합니다.");
        }
    }

    // 도착값 검증
    private static void validateActualDistance(BigDecimal actualDistance) {
        if (actualDistance == null || actualDistance.signum() < 0) {
            throw new IllegalArgumentException("실제 거리는 0 이상이어야 합니다.");
        }
    }

    private static void validateActualDuration(int actualDuration) {
        if (actualDuration < 0) {
            throw new IllegalArgumentException("실제 소요 시간은 0 이상이어야 합니다.");
        }
    }
}
