package com.portfolio.ip_geolocation_api.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.portfolio.ip_geolocation_api.domain.exception.IpAddrAlreadyExistsException;
import com.portfolio.ip_geolocation_api.domain.exception.IpAddrNotFoundException;
import com.portfolio.ip_geolocation_api.domain.exception.InvalidIpAddrDataException;
import com.portfolio.ip_geolocation_api.infrastructure.adapter.out.persistence.IpCachePersistenceException;
import com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest.GeolocationAuthenticationException;
import com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest.GeolocationRateLimitedException;
import com.portfolio.ip_geolocation_api.infrastructure.adapter.out.rest.GeolocationServiceUnavailableException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IpAddrNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(IpAddrNotFoundException e, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "IP_NOT_FOUND", e.getMessage(), null, request);
    }

    @ExceptionHandler(IpAddrAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAlreadyExists(IpAddrAlreadyExistsException e, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "IP_ALREADY_EXISTS", e.getMessage(), null, request);
    }

    @ExceptionHandler(InvalidIpAddrDataException.class)
    public ResponseEntity<ApiError> handleInvalidData(InvalidIpAddrDataException e, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_IP_DATA", e.getMessage(), null, request);
    }

    @ExceptionHandler(GeolocationServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailable(
            GeolocationServiceUnavailableException e, HttpServletRequest request) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, "GEOLOCATION_UNAVAILABLE",
                e.getMessage(), null, request);
    }

    @ExceptionHandler(GeolocationRateLimitedException.class)
    public ResponseEntity<ApiError> handleRateLimited(
            GeolocationRateLimitedException e, HttpServletRequest request) {
        return error(HttpStatus.TOO_MANY_REQUESTS, "GEOLOCATION_RATE_LIMITED",
                e.getMessage(), null, request);
    }

    @ExceptionHandler(GeolocationAuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(
            GeolocationAuthenticationException e, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "GEOLOCATION_AUTH_FAILED",
                e.getMessage(), null, request);
    }

    @ExceptionHandler(IpCachePersistenceException.class)
    public ResponseEntity<ApiError> handleCachePersistence(
            IpCachePersistenceException e, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "CACHE_PERSISTENCE_ERROR",
                e.getMessage(), null, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError(fieldError))
                .toList();
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Request validation failed", details, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException e,
            HttpServletRequest request) {
        List<String> details = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Request validation failed", details, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException e,
            HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST",
                "Request body is missing or malformed", null, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH",
                "Invalid value for path variable: " + e.getName(), null, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException e,
            HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER",
                "Required request parameter '" + e.getParameterName() + "' is not present", null, request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException e,
            HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND",
                "No resource found for: " + e.getResourcePath(), null, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", e.getMessage(), null, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception e, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred", null, request);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status, String code, String message, List<String> details,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                details,
                request.getRequestURI());
        return ResponseEntity.status(status).body(apiError);
    }

    private String fieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}