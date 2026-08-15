package com.iftrue.delivery.application.service.delivery;

import com.iftrue.delivery.application.dto.delivery.CreatedDelivery;
import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;
import com.iftrue.delivery.application.service.deliverymanager.DeliveryManagerAssignmentService;
import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryRepository;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.infrastructure.client.dto.CompanyResponse;
import com.iftrue.delivery.infrastructure.client.dto.HubRouteResponse;
import com.iftrue.delivery.infrastructure.client.dto.HubRouteSegment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryCreateTransactionService {

    private final DeliveryManagerAssignmentService deliveryManagerAssignmentService;
    private final DeliveryRepository deliveryRepository;

    public CreatedDelivery create(
            DeliveryCreateCommand command,
            CompanyResponse supplierCompany,
            CompanyResponse recipientCompany,
            HubRouteResponse shortestRoute
    ) {
        Delivery delivery = Delivery.create(
                command.orderId(),
                supplierCompany.hubId(),
                recipientCompany.hubId(),
                recipientCompany.companyAddress(),
                command.requesterName(),
                command.requesterSlackId()
        );

        UUID firstHubDeliveryManagerId = null;
        List<CreatedDelivery.TransitRouteInfo> transitRoutesInfo = new ArrayList<>();

        if (shortestRoute.isSameHub()) {
            DeliveryManager manager =
                    deliveryManagerAssignmentService.nextHubManager();

            firstHubDeliveryManagerId = manager.getHubId();

            delivery.addSameHubRoute(manager);
        } else {
            for (HubRouteSegment segment : shortestRoute.segments()) {

                DeliveryManager manager =
                        deliveryManagerAssignmentService.nextHubManager();

                if (firstHubDeliveryManagerId == null) {
                    firstHubDeliveryManagerId = manager.getHubId(); // NOTE: 최초 처음 허브 배송 담당자 id를 담기 위한 로직
                }

                if (!segment.arrivalHubId().equals(recipientCompany.hubId())) {
                    transitRoutesInfo.add(
                            new CreatedDelivery.TransitRouteInfo(
                                    segment.arrivalHubId(),
                                    segment.sequence(),
                                    segment.duration()
                            )
                    );
                }

                delivery.addRoute(
                        manager,
                        segment.departureHubId(),
                        segment.arrivalHubId(),
                        segment.sequence(),
                        segment.distance(),
                        segment.duration()
                );

            }
        }
        Delivery savedDelivery = deliveryRepository.save(delivery);

        return CreatedDelivery.of(
                savedDelivery,
                transitRoutesInfo,
                firstHubDeliveryManagerId
        );
    }
}



