package com.iftrue.delivery.application.service.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;
import com.iftrue.delivery.application.dto.delivery.DeliveryCreateResult;
import com.iftrue.delivery.domain.delivery.DeliveryRepository;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import com.iftrue.delivery.infrastructure.client.CompanyClient;
import com.iftrue.delivery.infrastructure.client.HubClient;
import com.iftrue.delivery.infrastructure.client.dto.CompanyResponse;
import com.iftrue.delivery.infrastructure.client.dto.HubRouteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeliveryCreateService {

    private final HubClient hubClient;
    private final DeliveryRepository deliveryRepository;
    private final CompanyClient companyClient;
    private final DeliveryCreateTransactionService transactionService;

    public DeliveryCreateResult create(DeliveryCreateCommand command) {

        if (deliveryRepository.existsByOrderId(command.orderId())) {
            throw new DeliveryServiceException(ErrorCode.DELIVERY_DUPLICATE, Map.of("orderId", command.orderId()));
        }

        CompanyResponse supplierCompany = companyClient.getCompany(command.supplierCompanyId()).data();
        CompanyResponse recipientCompany = companyClient.getCompany(command.recipientCompanyId()).data();

        HubRouteResponse shortestRoute =
                hubClient.getShortestRoute(
                        supplierCompany.hubId(),
                        recipientCompany.hubId()
                ).data();

        DeliveryCreateResult deliveryCreateResult = transactionService.create(
                command,
                supplierCompany,
                recipientCompany,
                shortestRoute
        );

        // TODO: AI 호출

        return deliveryCreateResult;
    }
}
