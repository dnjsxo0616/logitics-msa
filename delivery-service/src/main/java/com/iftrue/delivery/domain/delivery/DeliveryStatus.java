package com.iftrue.delivery.domain.delivery;

public enum DeliveryStatus {
    WAITING_AT_DEPARTURE_HUB,
    MOVING_BETWEEN_HUBS,
    ARRIVED_AT_DESTINATION_HUB,
    MOVING_TO_COMPANY,
    DELIVERED,
    CANCELLED,
}
