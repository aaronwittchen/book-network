package com.onion.book_network.handler;

import java.time.LocalDateTime;
import java.util.List;

public class ApiResponseFactory {

    /**
     * Success response with data and message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Success response with only data (no message).
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Success response with only a message.
     */
    public static ApiResponse<String> success(String message) {
        return ApiResponse.<String>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Success response with no data or message (for operations like delete).
     */
    public static ApiResponse<Void> success() {
        return ApiResponse.<Void>builder()
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Failure response with a message and list of errors.
     */
    public static <T> ApiResponse<T> failure(String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Failure response with only a message.
     */
    public static <T> ApiResponse<T> failure(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Failure response with only a list of errors.
     */
    public static <T> ApiResponse<T> failure(List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Failure response with a single error message.
     */
    public static <T> ApiResponse<T> failure(String message, String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(List.of(error))
                .timestamp(LocalDateTime.now())
                .build();
    }
}