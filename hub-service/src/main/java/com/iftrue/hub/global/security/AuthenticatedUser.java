package com.iftrue.hub.global.security;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, String role) {
}
