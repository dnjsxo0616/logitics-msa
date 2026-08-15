package com.iftrue.delivery.domain.delivery;

import com.iftrue.delivery.domain.common.DeletableEntity;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.domain.deliveryroute.DeliveryRoute;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.iftrue.delivery.domain.common.DomainValidator.requireNonNull;
import static com.iftrue.delivery.domain.common.DomainValidator.requireText;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_delivery")
public class Delivery extends DeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "destination_hub_id", nullable = false)
    private UUID destinationHubId;

    @Column(name = "company_delivery_manager_id")
    private UUID companyDeliveryManagerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DeliveryStatus status;

    @Column(name = "delivery_address", nullable = false, length = 255)
    private String deliveryAddress;

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(name = "recipient_slack_id", nullable = false, length = 50)
    private String recipientSlackId;

    @OneToMany(
            mappedBy = "delivery",
            cascade = CascadeType.PERSIST
    )
    @OrderBy("sequence ASC")
    @Getter(AccessLevel.NONE)
    private List<DeliveryRoute> deliveryRoutes = new ArrayList<>();

    private Delivery(
            UUID orderId,
            UUID departureHubId,
            UUID destinationHubId,
            String deliveryAddress,
            String recipientName,
            String recipientSlackId
    ) {
        this.orderId = requireNonNull(orderId, "주문 ID는 필수입니다."); // TODO: 예외처리는 정리하여 추후 맞는 예외로 모두 수정
        this.departureHubId = requireNonNull(departureHubId, "출발 허브 ID는 필수입니다.");
        this.destinationHubId = requireNonNull(destinationHubId, "도착 허브 ID는 필수입니다.");
        this.deliveryAddress = requireText(deliveryAddress, "배송 주소는 필수입니다.");
        this.recipientName = requireText(recipientName, "수령인 이름은 필수입니다.");
        this.recipientSlackId = requireText(recipientSlackId, "수령인 Slack ID는 필수입니다.");
        this.status = DeliveryStatus.WAITING_AT_DEPARTURE_HUB;
    }

    public static Delivery create(
            UUID orderId,
            UUID departureHubId,
            UUID destinationHubId,
            String deliveryAddress,
            String recipientName,
            String recipientSlackId
    ) {

        return new Delivery(
                orderId,
                departureHubId,
                destinationHubId,
                deliveryAddress,
                recipientName,
                recipientSlackId
        );
    }

    // 배송 경로 구성
    public void addRoute(
            DeliveryManager manager,
            UUID departureHubId,
            UUID arrivalHubId,
            int sequence,
            BigDecimal expectedDistance,
            Integer expectedDuration
    ) {
        if (status != DeliveryStatus.WAITING_AT_DEPARTURE_HUB) {
            throw new IllegalStateException("출발 허브 대기 상태에서만 배송 경로를 추가할 수 있습니다.");
        }

        validateRouteSequence(sequence);

        DeliveryRoute route = DeliveryRoute.create(
                this,
                manager,
                departureHubId,
                arrivalHubId,
                sequence,
                expectedDistance,
                expectedDuration
        );

        deliveryRoutes.add(route);
    }

    public void addSameHubRoute(DeliveryManager manager) {
        if (!departureHubId.equals(destinationHubId)) {
            throw new IllegalStateException("동일 허브 배송이 아닙니다.");
        }

        addRoute(
                manager,
                departureHubId,
                destinationHubId,
                1,
                BigDecimal.ZERO,
                0
        );
    }

    // 허브 배송
    public void startRoute(UUID routeId, Instant departedAt) {
        if (status != DeliveryStatus.WAITING_AT_DEPARTURE_HUB &&
                status != DeliveryStatus.MOVING_BETWEEN_HUBS) {
            throw new IllegalStateException("허브 배송 경로를 시작할 수 없는 배송 상태입니다.");
        }

        DeliveryRoute route = findRoute(routeId);
        validatePreviousRouteArrived(route);
        route.depart(departedAt);

        this.status = DeliveryStatus.MOVING_BETWEEN_HUBS;
    }

    public void arriveRoute(
            UUID routeId,
            Instant arrivedAt
    ) {
        if (status != DeliveryStatus.MOVING_BETWEEN_HUBS) {
            throw new IllegalStateException("허브 간 이동 중인 배송의 경로만 도착 처리할 수 있습니다.");
        }
        DeliveryRoute route = findRoute(routeId);

        route.arrive(arrivedAt);


        if (allRoutesArrived()) {
            this.status = DeliveryStatus.ARRIVED_AT_DESTINATION_HUB;
        }

    }

    // 업체 배송
    public void assignCompanyManager(DeliveryManager manager) {
        requireNonNull(manager, "배송담당자는 필수입니다.");

        validateCompanyManagerAssignmentStatus();
        manager.validateCompanyDeliveryAssignable(destinationHubId);

        this.companyDeliveryManagerId = manager.getId();
    }

    public void startCompanyDelivery() {
        if (status != DeliveryStatus.ARRIVED_AT_DESTINATION_HUB) {
            throw new IllegalStateException("목적지 허브 도착 상태에서만 업체 배송을 시작할 수 있습니다.");
        }

        if (companyDeliveryManagerId == null) {
            throw new IllegalStateException("업체 배송담당자가 배정되지 않았습니다.");
        }

        this.status = DeliveryStatus.MOVING_TO_COMPANY;
    }

    public void completeDelivery() {
        if (status != DeliveryStatus.MOVING_TO_COMPANY) {
            throw new IllegalStateException("업체 배송 중인 배송만 완료할 수 있습니다.");
        }

        this.status = DeliveryStatus.DELIVERED;
    }

    // 배송 취소
    public void cancel() {
        if (status != DeliveryStatus.WAITING_AT_DEPARTURE_HUB) {
            throw new DeliveryServiceException(
                    ErrorCode.DELIVERY_CANNOT_BE_CANCELLED
            );
        }

        this.status = DeliveryStatus.CANCELLED;
    }


    // 조회성 도메인 메서드
    private DeliveryRoute findRoute(UUID routeId) {
        requireNonNull(routeId, "배송 경로 ID는 필수입니다.");

        return deliveryRoutes.stream()
                .filter(route -> route.hasId(routeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 배송에 속한 배송 경로가 아닙니다."));
    }

    private boolean allRoutesArrived() {
        if (deliveryRoutes.isEmpty()) {
            throw new IllegalStateException("배송 경로가 존재하지 않습니다.");
        }
        return deliveryRoutes.stream()
                .allMatch(DeliveryRoute::isArrived);
    }

    // 검증
    private void validateCompanyManagerAssignmentStatus() {
        if (status != DeliveryStatus.ARRIVED_AT_DESTINATION_HUB) {
            throw new IllegalStateException("목적지 허브에 도착한 배송만 업체 배송담당자를 배정할 수 있습니다.");
        }

//        if (companyDeliveryManagerId != null) { // NOTE: 재배정 막을시 넣을것, 지금은 수정도 가능할거같아 주석처리
//            throw new IllegalStateException("이미 업체 배송담당자가 배정되어 있습니다.");
//        }
    }

    private void validatePreviousRouteArrived(
            DeliveryRoute targetRoute
    ) {
        if (targetRoute.isFirstRoute()) {
            return;
        }

        DeliveryRoute previousRoute = deliveryRoutes.stream()
                .filter(route ->
                        route.hasSequence(
                                targetRoute.getSequence() - 1
                        )
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("이전 순번의 배송 경로가 없습니다.")
                );

        if (!previousRoute.isArrived()) {
            throw new IllegalStateException("이전 배송 경로가 도착해야 다음 경로를 시작할 수 있습니다.");
        }
    }

    private void validateRouteSequence(int sequence) {
        boolean duplicated = deliveryRoutes.stream()
                .anyMatch(route -> route.hasSequence(sequence));

        if (duplicated) {
            throw new IllegalArgumentException("배송 경로 순번은 중복될 수 없습니다.");
        }
    }

}
