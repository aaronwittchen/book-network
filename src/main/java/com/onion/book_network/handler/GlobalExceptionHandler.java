package com.onion.book_network.handler;

import com.onion.book_network.exception.ActivationTokenException;
import com.onion.book_network.exception.OperationNotPermittedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiResponse<Void> buildErrorResponse(BusinessErrorCodes errorCode, List<String> details) {
        return ApiResponseFactory.failure(errorCode.getDescription(), details);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLockedException(LockedException ex) {
        BusinessErrorCodes error = BusinessErrorCodes.ACCOUNT_LOCKED;
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(buildErrorResponse(error, List.of(ex.getMessage())));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabledException(DisabledException ex) {
        BusinessErrorCodes error = BusinessErrorCodes.ACCOUNT_DISABLED;
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(buildErrorResponse(error, List.of(ex.getMessage())));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        BusinessErrorCodes error = BusinessErrorCodes.BAD_CREDENTIALS;
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(buildErrorResponse(error, List.of("Code: " + error.getCode())));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessagingException(MessagingException ex) {
        return ResponseEntity
                .status(500)
                .body(ApiResponseFactory.failure("Email sending failed", List.of(ex.getMessage())));
    }

    @ExceptionHandler(ActivationTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleActivationTokenException(ActivationTokenException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponseFactory.failure("Activation token invalid or expired", List.of(ex.getMessage())));
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ApiResponse<Void>> handleOperationNotPermitted(OperationNotPermittedException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponseFactory.failure("Operation not permitted", List.of(ex.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> validationErrors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity
                .badRequest()
                .body(ApiResponseFactory.failure("Validation failed", validationErrors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        ex.printStackTrace(); // Optional: log to monitoring
        return ResponseEntity
                .status(500)
                .body(ApiResponseFactory.failure("Internal server error, please contact admin", List.of(ex.getMessage())));
    }
}
