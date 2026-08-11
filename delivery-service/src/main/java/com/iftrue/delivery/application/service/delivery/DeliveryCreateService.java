package com.iftrue.delivery.application.service.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;
import com.iftrue.delivery.application.dto.delivery.DeliveryCreateResult;
import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryRepository;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import com.iftrue.delivery.infrastructure.client.HubClient;
import com.iftrue.delivery.infrastructure.client.dto.HubRouteResponse;
import com.iftrue.delivery.infrastructure.client.dto.HubRouteSegment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeliveryCreateService {

    private final HubClient hubClient;
    private final DeliveryRepository deliveryRepository;


    @Transactional
    public DeliveryCreateResult create(DeliveryCreateCommand command) {

        if (deliveryRepository.existsByOrderId(command.orderId())) {
            throw new DeliveryServiceException(ErrorCode.DELIVERY_DUPLICATE, Map.of("orderId", command.orderId()));
        }

        HubRouteResponse shortestRoute =
                hubClient.getShortestRoute(
                        command.departureHubId(),
                        command.destinationHubId()
                ).data();

        Delivery delivery = Delivery.create(
                command.orderId(),
                command.departureHubId(),
                command.destinationHubId(),
                command.deliveryAddress(),
                command.recipientName(),
                command.recipientSlackId()
        );

        if (shortestRoute.isSameHub()) {
            delivery.addSameHubRoute();
        } else {
            for (HubRouteSegment segment : shortestRoute.segments()) {
                delivery.addRoute(
                        segment.departureHubId(),
                        segment.arrivalHubId(),
                        segment.sequence(),
                        segment.distance(),
                        segment.duration()
                );
            }
        }


        Delivery savedDelivery = deliveryRepository.save(delivery);

        // TODO: 배송 생성 후 AI 마감 시간 계산 호출

        return new DeliveryCreateResult(savedDelivery.getId());
    }
}
