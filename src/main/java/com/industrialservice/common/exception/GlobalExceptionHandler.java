package com.industrialservice.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiError(Instant.now(), 400, "Validation Failed",
                "One or more fields are invalid.", req.getRequestURI(), errors));
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    ResponseEntity<ApiError> authenticationFailure(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(401).body(new ApiError(Instant.now(), 401, "Unauthorized",
                "Invalid email or password, or authentication is required.", req.getRequestURI(), Map.of()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(new ApiError(Instant.now(), 400, "Bad Request",
                ex.getMessage(), req.getRequestURI(), Map.of()));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    ResponseEntity<ApiError> forbidden(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(403).body(new ApiError(Instant.now(), 403, "Forbidden",
                "You do not have permission to perform this action.", req.getRequestURI(), Map.of()));
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    ResponseEntity<ApiError> notFound(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(404).body(new ApiError(Instant.now(), 404, "Not Found",
                ex.getMessage(), req.getRequestURI(), Map.of()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(Instant.now(), 500,
                "Internal Server Error", "An unexpected error occurred.", req.getRequestURI(), Map.of()));
    }
}
