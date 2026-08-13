package com.iftrue.delivery.application.service.deliverymanager;

import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerRepository;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManagerType;
import com.iftrue.delivery.global.exception.DeliveryServiceException;
import com.iftrue.delivery.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerCreateService {
    private static final int MAX_MANAGER_COUNT = 10;

    private final DeliveryManagerRepository deliveryManagerRepository;

    @Transactional
    public UUID create(DeliveryManagerCreateCommand command) {
        // TODO: 동시성 부분 추후 볼것

        if(deliveryManagerRepository.existsById(command.userId())){
            throw new DeliveryServiceException(ErrorCode.DELIVERY_MANAGER_DUPLICATE);
        }
        return switch (command.type()) {
            case HUB -> createHubManager(command);
            case COMPANY -> createCompanyManager(command);
        };
    }

    private UUID createHubManager(DeliveryManagerCreateCommand command) {
        if(command.hubId() != null) {
            throw new DeliveryServiceException(ErrorCode.DELIVERY_MANAGER_INVALID_HUB);
        }
        long activeCount =
                deliveryManagerRepository.countByTypeAndDeletedAtIsNull(DeliveryManagerType.HUB);

        validateManagerCount(activeCount);

        int nextSequence =
                deliveryManagerRepository
                        .findTopByTypeOrderBySequenceDesc(DeliveryManagerType.HUB)
                        .map(manager -> manager.getSequence() + 1)
                        .orElse(1);

        DeliveryManager deliveryManager = DeliveryManager.createHubManager(
                command.userId(),
                command.slackId(),
                nextSequence
        );
        return deliveryManagerRepository.save(deliveryManager).getId();
    }

    private UUID createCompanyManager(DeliveryManagerCreateCommand command) {

        if (command.hubId() == null) {
            throw new DeliveryServiceException(
                    ErrorCode.DELIVERY_MANAGER_INVALID_HUB
            );
        }

        UUID hubId = command.hubId();

        long activeCount =
                deliveryManagerRepository.countByTypeAndHubIdAndDeletedAtIsNull(DeliveryManagerType.COMPANY, hubId);

        validateManagerCount(activeCount);

        int nextSequence = deliveryManagerRepository.findTopByTypeAndHubIdOrderBySequenceDesc(
                        DeliveryManagerType.COMPANY,
                        hubId
                )
                .map(manager -> manager.getSequence() + 1)
                .orElse(1);

        DeliveryManager deliveryManager = DeliveryManager.createCompanyManager(
                command.userId(),
                command.slackId(),
                hubId,
                nextSequence
        );
        return deliveryManagerRepository.save(deliveryManager).getId();

    }

    private void validateManagerCount(long activeCount) {
        if (activeCount >= MAX_MANAGER_COUNT) {
            throw new DeliveryServiceException(
                    ErrorCode.DELIVERY_MANAGER_LIMIT_EXCEEDED
            );
        }
    }


}
