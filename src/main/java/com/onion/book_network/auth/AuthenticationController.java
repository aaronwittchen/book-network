package com.onion.book_network.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.mail.MessagingException;

import com.onion.book_network.handler.ApiResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        logger.info("Register request for email: {}", request.getEmail());
        service.register(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.<String>builder()
                        .success(true)
                        .message("Registration successful. Activation email sent to " + request.getEmail())
                        .build());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        AuthenticationResponse authResponse = service.authenticate(request);
        return ResponseEntity.ok(
                ApiResponse.<AuthenticationResponse>builder()
                        .success(true)
                        .data(authResponse)
                        .message("Authentication successful")
                        .build()
        );
    }

    @GetMapping("/activate-account")
    public ResponseEntity<ApiResponse<Void>> activate(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Account activated successfully")
                        .build()
        );
    }
}
