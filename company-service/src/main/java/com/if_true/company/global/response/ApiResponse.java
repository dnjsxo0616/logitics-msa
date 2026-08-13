package com.if_true.company.global.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime timestamp,
	int status,
	String code,
	T data
) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(
			LocalDateTime.now(),
			HttpStatus.OK.value(),
			"success",
			data
		);
	}
}
