package com.iftrue.order.global.security;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String role,
        UUID companyId
) {
}