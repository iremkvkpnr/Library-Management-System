package com.iremkvkpnr.librarymanagement;

import com.iremkvkpnr.librarymanagement.model.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {
    @Test
    void builderAndGettersShouldWork() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .phone("1234567890")
                .role(User.Role.PATRON)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("1234567890", user.getPhone());
        assertEquals(User.Role.PATRON, user.getRole());
    }

    @Test
    void roleEnumShouldWork() {
        assertEquals(User.Role.LIBRARIAN, User.Role.valueOf("LIBRARIAN"));
        assertEquals(User.Role.PATRON, User.Role.valueOf("PATRON"));
    }

    @Test
    void prePersistShouldSetCreatedAt() throws Exception {
        User user = new User();
        java.lang.reflect.Method method = User.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(user);
        assertNotNull(user.getCreatedAt());
        assertTrue(user.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
} 