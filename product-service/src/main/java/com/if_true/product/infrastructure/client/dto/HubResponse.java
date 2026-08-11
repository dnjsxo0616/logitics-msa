package com.if_true.product.infrastructure.client.dto;

import java.util.UUID;

public record HubResponse(
	UUID id,
	String hubName
) {
}
