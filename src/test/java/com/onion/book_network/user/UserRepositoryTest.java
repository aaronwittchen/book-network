package com.onion.book_network.user;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "spring.jpa.show-sql=true"
})
class UserRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        log.info("=== Testing context loading ===");
        assertNotNull(userRepository);
        log.info("✓ UserRepository successfully injected");
    }
    
    @Test
    void testSaveAndFindUser() {
        log.info("=== Testing save and find user ===");
        
        // Given
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("password");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEnabled(true);
        user.setAccountLocked(false);
        
        log.info("Created user object: email={}, firstname={}, lastname={}", 
                 user.getEmail(), user.getFirstname(), user.getLastname());

        // When
        log.info("Saving user to database...");
        User savedUser = userRepository.save(user);
        log.info("✓ User saved with ID: {}", savedUser.getId());
        
        log.info("Searching for user by email: {}", "test@mail.com");
        Optional<User> foundUser = userRepository.findByEmail("test@mail.com");

        // Then
        log.info("Verifying test results...");
        assertAll(
            () -> {
                assertNotNull(savedUser.getId());
                log.info("✓ Saved user has ID: {}", savedUser.getId());
            },
            () -> {
                assertTrue(foundUser.isPresent());
                log.info("✓ User found by email");
            },
            () -> {
                assertEquals("test@mail.com", foundUser.get().getEmail());
                log.info("✓ Email matches: {}", foundUser.get().getEmail());
            },
            () -> {
                assertEquals("Test", foundUser.get().getFirstname());
                log.info("✓ Firstname matches: {}", foundUser.get().getFirstname());
            },
            () -> {
                assertEquals("User", foundUser.get().getLastname());
                log.info("✓ Lastname matches: {}", foundUser.get().getLastname());
            }
        );
        
        log.info("=== Test completed successfully ===");
    }

    @Test
    void testFindByNonExistentEmail() {
        log.info("=== Testing find by non-existent email ===");
        
        String nonExistentEmail = "nonexistent@mail.com";
        log.info("Searching for non-existent email: {}", nonExistentEmail);
        
        // When
        Optional<User> foundUser = userRepository.findByEmail(nonExistentEmail);
        
        // Then
        assertFalse(foundUser.isPresent());
        log.info("✓ Correctly returned empty Optional for non-existent email");
        log.info("=== Test completed successfully ===");
    }

    @Test
    void testExistsByEmail() {
        log.info("=== Testing existsByEmail method ===");
        
        // Given
        User user = new User();
        user.setEmail("exists@mail.com");
        user.setPassword("password");
        user.setFirstname("Existing");
        user.setLastname("User");
        user.setEnabled(true);
        user.setAccountLocked(false);
        
        log.info("Creating user with email: {}", user.getEmail());
        userRepository.save(user);
        log.info("✓ User saved to database");

        // When & Then
        log.info("Checking if email exists: {}", "exists@mail.com");
        boolean exists = userRepository.existsByEmail("exists@mail.com");
        assertTrue(exists);
        log.info("✓ existsByEmail returned true for existing email");
        
        log.info("Checking if non-existent email exists: {}", "notexists@mail.com");
        boolean notExists = userRepository.existsByEmail("notexists@mail.com");
        assertFalse(notExists);
        log.info("✓ existsByEmail returned false for non-existent email");
        
        log.info("=== Test completed successfully ===");
    }
}