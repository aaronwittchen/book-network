package com.onion.book_network.role;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveRole() {
        Role role = Role.builder()
                        .name("ROLE_USER")
                        .build();

        Role savedRole = roleRepository.save(role);

        assertNotNull(savedRole.getId());
        assertEquals("ROLE_USER", savedRole.getName());
    }

    @Test
    void testFindByName() {
        Role role = Role.builder()
                        .name("ROLE_USER")
                        .build();
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName("ROLE_USER");
        assertTrue(found.isPresent());
        assertEquals("ROLE_USER", found.get().getName());
    }

    @Test
    void testGetDisplayName() {
        Role role = Role.builder()
                        .name("ROLE_USER")
                        .build();

        assertEquals("user", role.getDisplayName());
    }

    @Test
    void testEqualsAndHashCode() {
        Role role1 = Role.builder().name("ROLE_USER").build();
        Role role2 = Role.builder().name("ROLE_USER").build();

        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testToString() {
        Role role = Role.builder().name("ROLE_USER").build();
        String str = role.toString();
        assertTrue(str.contains("ROLE_USER"));
    }
}
