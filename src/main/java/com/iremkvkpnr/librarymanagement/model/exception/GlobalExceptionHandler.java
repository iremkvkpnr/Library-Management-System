package com.iremkvkpnr.librarymanagement.model.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        log.warn("Validation error: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFoundException(BookNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        log.error("Book not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid input: " + message);
        log.warn("HttpMessageNotReadableException: {}", message);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<Map<String, String>> handleUserValidationException(UserValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        log.warn("User validation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(UserPrincipalNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserPrincipalNotFoundException(UserPrincipalNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(BookValidationException.class)
    public ResponseEntity<Map<String, String>> handleBookValidationException(BookValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        log.warn("Book validation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BorrowingValidationException.class)
    public ResponseEntity<Map<String, String>> handleBorrowingValidationException(BorrowingValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        log.warn("Borrowing validation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BorrowingNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBorrowingNotFoundException(BorrowingNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        log.error("Borrowing not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Conflict: " + ex.getMostSpecificCause().getMessage());
        log.error("Conflict (409): {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Access denied: You do not have permission to perform this action.");
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);  // 403
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Authentication failed: " + ex.getMessage());
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);  // 401
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Internal server error: " + ex.getMessage());
        log.error("Internal server error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token süresi dolmuş");
    }
}
