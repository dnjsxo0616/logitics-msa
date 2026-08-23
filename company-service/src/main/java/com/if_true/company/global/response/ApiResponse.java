package com.if_true.company.global.response;

import java.time.Instant;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
	Instant timestamp,
	int status,
	String code,
	T data
) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(
			Instant.now(),
			HttpStatus.OK.value(),
			"success",
			data
		);
	}
}
