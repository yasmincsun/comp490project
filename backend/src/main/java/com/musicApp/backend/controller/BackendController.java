/**
 * Date: September 25, 2025
 * @author Jose Bastidas
 */


package com.musicApp.backend.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.NoSuchFileException;
import java.util.Map;

/**
 * Global exception handler for the MusicApp backend. This class centralizes
 * the handling of common exceptions thrown across all controllers, ensuring
 * consistent and user-friendly error responses throughout the application.
 
 * The class uses Spring Boot's @ControllerAdvice to intercept and process
 * exceptions globally instead of duplicating error-handling logic in each
 * controller.
 */
@ControllerAdvice
public class BackendController {

    /**
     * Handles missing or unreadable request body errors.
     * @param e the {@link HttpMessageNotReadableException} thrown when the request body is invalid or missing
     * @return a {@link ResponseEntity} containing an error message and HTTP 400 (Bad Request) status
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("message", "Required request body is missing."));
    }

    /**
     * Handles invalid or failed validation of method arguments.
     * @param e the {@link MethodArgumentNotValidException} containing validation errors for request fields
     * @return a {@link ResponseEntity} with detailed validation error messages and HTTP 400 (Bad Request) status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        return ResponseEntity.badRequest().body(Map.of("message", errorMessage.toString()));
    }

    /**
     * Handles 404 errors when requested resources are not found.
     * @param e the {@link NoResourceFoundException} thrown when the requested resource or endpoint does not exist
     * @return a {@link ResponseEntity} with an error message and HTTP 404 (Not Found) status
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    /**
     * Handles database constraint violations, such as duplicate entries.
     * @param e the {@link DataIntegrityViolationException} thrown when a database constraint is violated
     * @return a {@link ResponseEntity} with an explanatory message and HTTP 400 (Bad Request) status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        if (e.getMessage().contains("Duplicate entry")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists, please use another email or login."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    /**
     *  Handles missing HTTP request parameters.
     * @param e the {@link MissingServletRequestParameterException} thrown when a required parameter is not provided
     * @return a {@link ResponseEntity} containing an error message and HTTP 400 (Bad Request) status
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().body(Map.of("message", "Required request parameter is missing."));
    }

    /**
     *   Handles illegal or invalid method argument errors.
     * @param e the {@link IllegalArgumentException} or {@link IllegalStateException} thrown due to invalid method arguments
     * @return a {@link ResponseEntity} containing the exception message and HTTP 400 (Bad Request) status
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    /**
     * Handles cases where a requested file does not exist.
     * @param e the {@link NoSuchFileException} thrown when a file cannot be found
     * @return a {@link ResponseEntity} containing an error message and HTTP 404 (Not Found) status
     */
    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchFileException(NoSuchFileException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "File not found"));
    }

    /**
     * Catches all other unhandled exceptions and returns a generic error response.
     * @param e the generic {@link Exception} thrown during request processing
     * @return a {@link ResponseEntity} containing the exception message and HTTP 500 (Internal Server Error) status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
    }
}