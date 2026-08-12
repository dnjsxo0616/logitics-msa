package com.iftrue.hub.application.dto;

import java.util.UUID;

public record HubExistsResponseDto(
        UUID hubId,
        boolean exists
) {
}
