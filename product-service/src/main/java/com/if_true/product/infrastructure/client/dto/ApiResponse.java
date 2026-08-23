package com.if_true.product.infrastructure.client.dto;

import java.time.Instant;

public record ApiResponse<T>(
	Instant timestamp,
	int status,
	String code,
	T data
) {
}
