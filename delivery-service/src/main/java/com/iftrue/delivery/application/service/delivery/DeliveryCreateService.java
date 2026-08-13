package com.iftrue.delivery.application.service.delivery;

import com.iftrue.delivery.application.dto.delivery.CreatedDelivery;
import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;
import com.iftrue.delivery.application.dto.delivery.DeliveryCreateResult;
import com.iftrue.delivery.domain.delivery.DeliveryRepository;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import com.iftrue.delivery.infrastructure.client.CompanyClient;
import com.iftrue.delivery.infrastructure.client.HubClient;
import com.iftrue.delivery.infrastructure.client.NotificationClient;
import com.iftrue.delivery.infrastructure.client.UserClient;
import com.iftrue.delivery.infrastructure.client.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryCreateService {

    private final HubClient hubClient;
    private final DeliveryRepository deliveryRepository;
    private final CompanyClient companyClient;
    private final DeliveryCreateTransactionService transactionService;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    public DeliveryCreateResult create(DeliveryCreateCommand command) {

        if (deliveryRepository.existsByOrderId(command.orderId())) {
            throw new DeliveryServiceException(
                    ErrorCode.DELIVERY_DUPLICATE,
                    Map.of("orderId", command.orderId())
            );
        }

        CompanyResponse supplierCompany =
                companyClient.getCompany(command.supplierCompanyId()).data();

        CompanyResponse recipientCompany =
                companyClient.getCompany(command.recipientCompanyId()).data();

        HubRouteResponse shortestRoute =
                hubClient.getShortestRoute(
                        supplierCompany.hubId(),
                        recipientCompany.hubId()
                ).data();

        CreatedDelivery createdDelivery =
                transactionService.create(
                        command,
                        supplierCompany,
                        recipientCompany,
                        shortestRoute
                );

        HubResponse departureHub =
                hubClient.getHub(createdDelivery.departureHubId()).data();

        List<HubResponse> transitHubs =
                createdDelivery.deliveryRoutes().stream()
                        .map(CreatedDelivery.RouteInfo::arrivalHubId)
                        .filter(hubId ->
                                !hubId.equals(createdDelivery.destinationHubId())
                        )
                        .distinct()
                        .map(hubId -> hubClient.getHub(hubId).data())
                        .toList();

        UUID managerId = createdDelivery.deliveryRoutes()
                .get(0)
                .hubDeliveryManagerId();

        UserResponse manager =
                userClient.getUser(managerId).data();

        notificationClient.createDeliveryNotification(
                NotificationCreateRequest.of(
                        createdDelivery,
                        command,
                        supplierCompany,
                        recipientCompany,
                        departureHub,
                        transitHubs,
                        manager
                )
        );

        return DeliveryCreateResult.from(createdDelivery);
    }
}