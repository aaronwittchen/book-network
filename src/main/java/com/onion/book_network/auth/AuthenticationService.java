package com.onion.book_network.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import com.onion.book_network.exception.ActivationTokenException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onion.book_network.email.EmailService;
import com.onion.book_network.email.EmailTemplateName;
import com.onion.book_network.exception.OperationNotPermittedException;
import com.onion.book_network.role.RoleRepository;
import com.onion.book_network.security.JwtService;
import com.onion.book_network.user.Token;
import com.onion.book_network.user.TokenRepository;
import com.onion.book_network.user.User;
import com.onion.book_network.user.UserRepository;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public void register(RegistrationRequest request) throws MessagingException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new OperationNotPermittedException("Email already in use");
        }

        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new OperationNotPermittedException("ROLE USER was not initialized"));

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        logger.info("User registered: {}", request.getEmail());

        sendValidationEmail(user);
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var user = (User) auth.getPrincipal();
            logger.info("User authenticated: {}", user.getEmail());

            // Create claims for the token
            var claims = new HashMap<String, Object>();
            claims.put("fullName", user.getFullName());
            claims.put("email", user.getEmail());
            
            // Generate token with claims
            var jwtToken = jwtService.generateToken(claims, user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", request.getEmail(), e);
            throw e;
        }
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ActivationTokenException("Invalid activation token"));

        if (savedToken.getValidatedAt() != null) {
            throw new ActivationTokenException("Token has already been used");
        }

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new ActivationTokenException("Activation token expired. A new token has been sent to your email.");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

        logger.info("User account activated: {}", user.getEmail());
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        logger.debug("Activation token generated for user: {}", user.getEmail());
        return generatedToken;
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
        logger.info("Activation email sent to: {}", user.getEmail());
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
