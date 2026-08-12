package com.if_true.product.global.exception;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	ResponseEntity<ApiErrorResponse> handleNotFound(EntityNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ApiErrorResponse.of(404, "Not Found", exception.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException exception) {
		return ResponseEntity.badRequest()
			.body(ApiErrorResponse.of(400, "Bad Request", exception.getMessage()));
	}

	@ExceptionHandler(IllegalStateException.class)
	ResponseEntity<ApiErrorResponse> handleConflict(IllegalStateException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(ApiErrorResponse.of(409, "Conflict", exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.orElse("Invalid request");
		return ResponseEntity.badRequest()
			.body(ApiErrorResponse.of(400, "Bad Request", message));
	}

	@ExceptionHandler(FeignException.NotFound.class)
	ResponseEntity<ApiErrorResponse> handleFeignNotFound(FeignException.NotFound exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ApiErrorResponse.of(404, "Not Found", "Company not found."));
	}

	@ExceptionHandler(FeignException.class)
	ResponseEntity<ApiErrorResponse> handleFeign(FeignException exception) {
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
			.body(ApiErrorResponse.of(502, "Bad Gateway", "Failed to call external service."));
	}
}
