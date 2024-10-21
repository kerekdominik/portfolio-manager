package com.portfolio.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Initialize a User instance before each test
        user = new User();
    }

    @Test
    void testNoArgsConstructor() {
        User userNoArgs = new User();
        assertNotNull(userNoArgs);
    }

    @Test
    void testAllArgsConstructor() {
        User userAllArgs = new User(
                1L,
                "test@example.com",
                "testUser",
                "password123",
                "John",
                "Doe",
                Role.USER
        );

        assertEquals(1L, userAllArgs.getId());
        assertEquals("testUser", userAllArgs.getUsername());
        assertEquals("password123", userAllArgs.getPassword());
        assertEquals("test@example.com", userAllArgs.getEmail());
        assertEquals("John", userAllArgs.getFirstName());
        assertEquals("Doe", userAllArgs.getLastName());
        assertEquals(Role.USER, userAllArgs.getRole());
    }

    @Test
    void testSettersAndGetters() {
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.USER);

        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void testGetAuthorities() {
        user.setRole(Role.ADMIN);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        //assertEquals("ADMIN", authorities.iterator().next().getAuthority());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(user.isEnabled());
    }

    @Test
    void testBuilder() {
        User userBuilder = User.builder()
                .id(2L)
                .username("builderUser")
                .password("builderPass")
                .email("builder@example.com")
                .firstName("Builder")
                .lastName("User")
                .role(Role.USER)
                .build();

        assertEquals(2L, userBuilder.getId());
        assertEquals("builderUser", userBuilder.getUsername());
        assertEquals("builderPass", userBuilder.getPassword());
        assertEquals("builder@example.com", userBuilder.getEmail());
        assertEquals("Builder", userBuilder.getFirstName());
        assertEquals("User", userBuilder.getLastName());
        assertEquals(Role.USER, userBuilder.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User(
                1L,
                "testUser",
                "password123",
                "test@example.com",
                "John",
                "Doe",
                Role.USER
        );

        User user2 = new User(
                1L,
                "testUser",
                "password123",
                "test@example.com",
                "John",
                "Doe",
                Role.USER
        );

        User user3 = new User(
                2L,
                "anotherUser",
                "password456",
                "another@example.com",
                "Jane",
                "Smith",
                Role.ADMIN
        );

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.USER);

        String toString = user.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username=testUser"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("firstName=John"));
        assertTrue(toString.contains("lastName=Doe"));
        assertTrue(toString.contains("role=USER"));
    }
}
