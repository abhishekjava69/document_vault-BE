package com.documentvault.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AuthGlobalException {

	private static final Logger log = LoggerFactory.getLogger(AuthGlobalException.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(field -> field.getField(),
						field -> field.getDefaultMessage() == null ? "Invalid value" : field.getDefaultMessage(),
						(first, second) -> first));

		return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", request, errors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidBody(HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_BODY", "Request body is missing or invalid", request,
				Map.of());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", "A required request parameter is missing", request,
				Map.of());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER_TYPE", "A request parameter has an invalid value",
				request, Map.of());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "The requested resource was not found", request,
				Map.of());
	}

	@ExceptionHandler(DuplicateRegistrationException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicateRegistration(DuplicateRegistrationException ex,
			HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, "DUPLICATE_REGISTRATION", ex.getMessage(), request, Map.of());
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS", ex.getMessage(), request, Map.of());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleIntegrityViolation(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		log.warn("Database integrity constraint rejected request at {}", request.getRequestURI());

		return build(HttpStatus.CONFLICT, "DATA_CONFLICT", "The request conflicts with existing data", request,
				Map.of());
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ApiErrorResponse> handleDatabaseFailure(DataAccessException ex, HttpServletRequest request) {
		log.error("Database operation failed at {}", request.getRequestURI(), ex);

		return build(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR",
				"The request could not be completed due to a database error", request, Map.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		log.error("Unexpected error at {}", request.getRequestURI(), ex);

		return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
				"An unexpected error occurred. Please try again later", request, Map.of());
	}

	private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String message,
			HttpServletRequest request, Map<String, String> fieldErrors) {
		ApiErrorResponse response = new ApiErrorResponse(java.time.Instant.now(), status.value(),
				status.getReasonPhrase(), code, message, request.getRequestURI(), fieldErrors);

		return ResponseEntity.status(status).body(response);
	}
}
