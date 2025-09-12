package com.onion.book_network.auth;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

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
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Register a new user",
        requestBody = @RequestBody(
            required = true,
            description = "User registration payload",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{ \"firstname\": \"test\", \"lastname\": \"test\", \"email\": \"test@mail.com\", \"password\": \"PassWord123!\" }"
                )
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Registration successful",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "{ \"success\": true, \"message\": \"Registration successful. Activation email sent to test@mail.com\" }"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid input",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "{ \"success\": false, \"message\": \"Invalid input\" }"
                    )
                )
            )
        }
    )
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid RegistrationRequest request)
            throws MessagingException {
        logger.info("Register request for email: {}", request.getEmail());
        service.register(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.<String>builder()
                        .success(true)
                        .message("Registration successful. Activation email sent to " + request.getEmail())
                        .build());
    }

    @PostMapping("/authenticate")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Authenticate user and get JWT token",
        requestBody = @RequestBody(
            required = true,
            description = "User login payload",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{ \"email\": \"test1@mail.com\", \"password\": \"PassWord123!\" }"
                )
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Authentication successful",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "{ \"success\": true, \"data\": { \"token\": \"eyJhbGciOiJIUzI1NiJ9...\" }, \"message\": \"Authentication successful\" }"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Invalid credentials",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "{ \"success\": false, \"message\": \"Invalid email or password\" }"
                    )
                )
            )
        }
    )
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
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Activate account with token",
        parameters = {
            @Parameter(
                name = "token",
                description = "Activation token received by email",
                required = true,
                example = "YOUR_ACTIVATION_TOKEN"
            )
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Account activated successfully",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "{ \"success\": true, \"message\": \"Account activated successfully\" }"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid or expired token",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "{ \"success\": false, \"message\": \"Invalid or expired token\" }"
                    )
                )
            )
        }
    )
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
