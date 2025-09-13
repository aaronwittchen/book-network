/**
package com.onion.book_network.auth;

import com.onion.book_network.email.EmailService;
import com.onion.book_network.exception.OperationNotPermittedException;
import com.onion.book_network.role.Role;
import com.onion.book_network.role.RoleRepository;
import com.onion.book_network.security.JwtService;
import com.onion.book_network.user.Token;
import com.onion.book_network.user.TokenRepository;
import com.onion.book_network.user.User;
import com.onion.book_network.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private RoleRepository roleRepository;
    @Mock private EmailService emailService;
    @Mock private TokenRepository tokenRepository;

    @InjectMocks private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldThrowIfEmailExists() {
        RegistrationRequest req = RegistrationRequest.builder()
                .firstName("Test").lastName("User").email("test@mail.com").password("password11").build();
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(new User()));
        assertThatThrownBy(() -> authenticationService.register(req))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void register_shouldEncodePasswordAndSaveUserAndSendEmail() throws Exception {
        RegistrationRequest req = RegistrationRequest.builder()
                .firstName("Test").lastName("User").email("test@mail.com").password("password11").build();
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        Role userRole = Role.builder().id(1).name("USER").build();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(emailService).sendEmail(any(), any(), any(), any(), any(), any());
        doAnswer(inv -> inv.getArgument(0)).when(tokenRepository).save(any(Token.class));

        authenticationService.register(req);

        verify(userRepository).save(argThat(user -> user.getPassword().equals("encodedPassword")));
        verify(emailService).sendEmail(any(), any(), any(), any(), any(), any());
    }

    @Test
    void authenticate_shouldReturnJwtTokenOnSuccess() {
        AuthenticationRequest req = AuthenticationRequest.builder()
                .email("test@mail.com")
                .password("password11")
                .build();
        User user = User.builder().email("test@mail.com").password("encoded").firstName("Test").lastName("User").roles(List.of()).build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.generateToken(anyMap(), eq(user))).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.authenticate(req);
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void authenticate_shouldThrowOnBadCredentials() {
        AuthenticationRequest req = AuthenticationRequest.builder()
                .email("test@mail.com")
                .password("wrong")
                .build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        assertThatThrownBy(() -> authenticationService.authenticate(req))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void activateAccount_shouldEnableUserOnValidToken() throws Exception {
        User user = User.builder().id(1).email("test@mail.com").enabled(false).build();
        Token token = Token.builder().token("abc123").user(user).expiresAt(LocalDateTime.now().plusMinutes(10)).build();
        when(tokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenRepository.save(any(Token.class))).thenAnswer(inv -> inv.getArgument(0));

        authenticationService.activateAccount("abc123");
        assertThat(user.isEnabled()).isTrue();
        assertThat(token.getValidatedAt()).isNotNull();
    }

    @Test
    void activateAccount_shouldThrowOnExpiredToken() throws Exception {
        User user = User.builder().id(1).email("test@mail.com").enabled(false).build();
        Token token = Token.builder().token("abc123").user(user).expiresAt(LocalDateTime.now().minusMinutes(1)).build();
        when(tokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));
        doNothing().when(emailService).sendEmail(any(), any(), any(), any(), any(), any());
        assertThatThrownBy(() -> authenticationService.activateAccount("abc123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Activation token has expired");
    }
}

*/