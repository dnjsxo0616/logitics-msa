package com.iftrue.hub.application.dto;

import com.iftrue.hub.domain.Hub;

import java.math.BigDecimal;
import java.util.UUID;

public record HubResponseDto(
        UUID id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude
) {
    public static HubResponseDto from(Hub hub) {
        return new HubResponseDto(
                hub.getId(),
                hub.getName(),
                hub.getAddress(),
                hub.getLatitude(),
                hub.getLongitude()
        );
    }
}
