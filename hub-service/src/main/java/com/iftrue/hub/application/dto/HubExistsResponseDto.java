package com.iftrue.hub.application.dto;

import java.util.UUID;

public record HubExistsResponseDto(
        UUID hubId, boolean exists
) {
    public static HubExistsResponseDto of(UUID hubId, boolean exists) {
        return new HubExistsResponseDto(hubId, exists);
    }
}
