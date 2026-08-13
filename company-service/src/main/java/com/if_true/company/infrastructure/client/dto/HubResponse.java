package com.if_true.company.infrastructure.client.dto;

import java.util.UUID;

public record HubResponse(
	UUID id,
	String hubName
) {
}
