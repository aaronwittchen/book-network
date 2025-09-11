/*
package com.onion.book_network.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.HashMap;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    private final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long EXPIRATION = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(); // ideally inject SECRET_KEY and EXPIRATION via constructor
        userDetails = new User(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForWrongUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User(
                "other@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() {
        String token = jwtService.generateToken(userDetails);

        JwtService spyJwtService = spy(jwtService);
        doReturn(true).when(spyJwtService).isTokenExpired(anyString());

        assertFalse(spyJwtService.isTokenValid(token, userDetails));
        verify(spyJwtService).isTokenExpired(token);
    }

    @Test
    void extractAllClaims_ShouldThrow_ForInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.extractAllClaims(invalidToken));
    }

    @Test
    void extractUsername_ShouldThrow_ForInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.extractUsername(invalidToken));
    }

    @Test
    void generateToken_WithExtraClaims_ShouldIncludeClaims() {
        var extraClaims = new HashMap<String, Object>();
        extraClaims.put("custom", "value");

        String token = jwtService.generateToken(extraClaims, userDetails);
        assertNotNull(token);

        var claims = jwtService.extractAllClaims(token);
        assertEquals("value", claims.get("custom"));
        assertEquals(userDetails.getUsername(), claims.getSubject());
    }
}
*/