package com.iftrue.delivery.application.service.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;
import com.iftrue.delivery.application.dto.delivery.DeliveryCreateResult;
import com.iftrue.delivery.application.service.deliverymanager.DeliveryManagerAssignmentService;
import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.delivery.DeliveryRepository;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.domain.deliveryroute.DeliveryRoute;
import com.iftrue.delivery.infrastructure.client.dto.CompanyResponse;
import com.iftrue.delivery.infrastructure.client.dto.HubRouteResponse;
import com.iftrue.delivery.infrastructure.client.dto.HubRouteSegment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryCreateTransactionService {

    private final DeliveryManagerAssignmentService deliveryManagerAssignmentService;
    private final DeliveryRepository deliveryRepository;

    public DeliveryCreateResult create(
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

        if (shortestRoute.isSameHub()) {
            DeliveryRoute deliveryRoute = delivery.addSameHubRoute();

            DeliveryManager manager =
                    deliveryManagerAssignmentService.nextHubManager();

            deliveryRoute.assignHubDeliveryManager(manager);

        } else {
            for (HubRouteSegment segment : shortestRoute.segments()) {
                DeliveryRoute deliveryRoute = delivery.addRoute(
                        segment.departureHubId(),
                        segment.arrivalHubId(),
                        segment.sequence(),
                        segment.distance(),
                        segment.duration()
                );

                DeliveryManager manager =
                        deliveryManagerAssignmentService.nextHubManager();

                deliveryRoute.assignHubDeliveryManager(manager);
            }
        }
        Delivery savedDelivery = deliveryRepository.save(delivery);

        return new DeliveryCreateResult(savedDelivery.getId());
    }
}



