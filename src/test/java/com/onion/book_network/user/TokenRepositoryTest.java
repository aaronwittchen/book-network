package com.onion.book_network.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindByToken() {
        // Create a user first
        User user = User.builder()
                        .firstName("testuser")
                        .lastName("User")
                        .email("testuser@mail.com")
                        .password("password123")
                        .build();
        user = userRepository.save(user);

        // Create a token
        Token token = Token.builder()
                           .token("sample-token-123")
                           .createdAt(LocalDateTime.now())
                           .expiresAt(LocalDateTime.now().plusHours(1))
                           .user(user)
                           .build();

        Token savedToken = tokenRepository.save(token);

        assertNotNull(savedToken.getId());
        assertEquals("sample-token-123", savedToken.getToken());

        // Test findByToken
        Optional<Token> found = tokenRepository.findByToken("sample-token-123");
        assertTrue(found.isPresent());
        assertEquals(savedToken.getId(), found.get().getId());
        assertEquals(user.getId(), found.get().getUser().getId());
    }
}
