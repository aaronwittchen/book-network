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

import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleLockedException(LockedException ex) {
        return ResponseEntity
                .status(BusinessErrorCodes.ACCOUNT_LOCKED.getHttpStatus())
                .body(ExceptionResponse.fromBusinessError(BusinessErrorCodes.ACCOUNT_LOCKED, ex.getMessage()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleDisabledException(DisabledException ex) {
        return ResponseEntity
                .status(BusinessErrorCodes.ACCOUNT_DISABLED.getHttpStatus())
                .body(ExceptionResponse.fromBusinessError(BusinessErrorCodes.ACCOUNT_DISABLED, ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(BusinessErrorCodes.BAD_CREDENTIALS.getHttpStatus())
                .body(ExceptionResponse.fromBusinessError(BusinessErrorCodes.BAD_CREDENTIALS, "Login and/or password is incorrect"));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleMessagingException(MessagingException ex) {
        return ResponseEntity
                .status(500)
                .body(ExceptionResponse.builder()
                        .error(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ActivationTokenException.class)
    public ResponseEntity<ExceptionResponse> handleActivationTokenException(ActivationTokenException ex) {
        return ResponseEntity
                .badRequest()
                .body(ExceptionResponse.builder()
                        .error(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleOperationNotPermitted(OperationNotPermittedException ex) {
        return ResponseEntity
                .badRequest()
                .body(ExceptionResponse.builder()
                        .error(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Set<String> validationErrors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toSet());

        return ResponseEntity
                .badRequest()
                .body(ExceptionResponse.fromValidationErrors(validationErrors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        ex.printStackTrace(); // Optional: log to a monitoring system
        return ResponseEntity
                .status(500)
                .body(ExceptionResponse.builder()
                        .businessErrorDescription("Internal server error, please contact the admin")
                        .error(ex.getMessage())
                        .build());
    }
}
