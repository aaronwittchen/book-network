package com.onion.book_network.auth;

import com.onion.book_network.handler.ApiResponse;
import com.onion.book_network.handler.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthenticationController {

    private final AuthenticationService service;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Registration successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegistrationRequest request) throws MessagingException {
        logger.info("Register request for email: {}", request.getEmail());
        service.register(request);
        return ResponseEntity.status(201)
                .body(ApiResponseFactory.success(
                        "Registration successful. Activation email sent to " + request.getEmail()
                ));
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate user and get JWT token")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authentication successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse authResponse = service.authenticate(request);
        return ResponseEntity.ok(
                ApiResponseFactory.success(authResponse, "Authentication successful")
        );
    }

    @GetMapping("/activate-account")
    @Operation(summary = "Activate user account with activation token")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account activated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<ApiResponse<String>> activate(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
        return ResponseEntity.ok(
                ApiResponseFactory.success("Account activated successfully")
        );
    }
}
