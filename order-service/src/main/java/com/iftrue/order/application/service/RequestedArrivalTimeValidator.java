package com.iftrue.order.application.service;

import com.iftrue.order.global.exception.BusinessException;
import com.iftrue.order.global.exception.OrderErrorCode;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class RequestedArrivalTimeValidator {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Seoul");
    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_END = LocalTime.of(18, 0);

    public void validate(Instant requestedArrivalAt) {
        if (requestedArrivalAt == null
                || !requestedArrivalAt.isAfter(Instant.now())) {
            throw new BusinessException(OrderErrorCode.INVALID_REQUESTED_ARRIVAL_TIME);
        }

        ZonedDateTime localArrival = requestedArrivalAt.atZone(BUSINESS_ZONE);

        if (localArrival.getSecond() != 0 || localArrival.getNano() != 0) {
            throw new BusinessException(OrderErrorCode.INVALID_REQUESTED_ARRIVAL_TIME);
        }

        LocalTime localArrivalTime = localArrival.toLocalTime();
        if (localArrivalTime.isBefore(WORK_START)
                || localArrivalTime.isAfter(WORK_END)) {
            throw new BusinessException(OrderErrorCode.REQUESTED_ARRIVAL_OUTSIDE_WORKING_HOURS);
        }
    }
}
