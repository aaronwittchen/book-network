package com.onion.book_network.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
   
    private final AuthenticationService service;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
   
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @Valid RegistrationRequest request
    ) {
        logger.info("Received registration request for email: {}", request.getEmail());
        try {
            service.register(request);
            logger.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.accepted().body("User registered: " + request.getEmail());
        } catch (Exception e) {
            logger.error("Registration error for email {}: {}", request.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }
   
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            return ResponseEntity.ok(service.authenticate(request));
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Authentication error for email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
   
    @GetMapping("/activate-account")
    public ResponseEntity<Void> confirm(
            @RequestParam String token
    ) {
        try {
            service.activateAccount(token);
            return ResponseEntity.ok().build();
        } catch (MessagingException e) {
            logger.error("Account activation failed for token: {}", token, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Account activation error for token: {}", token, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}