package com.iremkvkpnr.librarymanagement.model.entity;

import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.UserResponse;
import com.iremkvkpnr.librarymanagement.model.mapper.UserMapper;
import com.iremkvkpnr.librarymanagement.model.exception.UserValidationException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    @Test
    void toEntity_shouldMapAllFields() {
        UserRequest req = new UserRequest("Test", "test@example.com", "password123", "+905551234567", User.Role.PATRON);
        User user = UserMapper.toEntity(req);
        assertEquals("Test", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("+905551234567", user.getPhone());
        assertEquals(User.Role.PATRON, user.getRole());
    }

    @Test
    void toEntity_shouldThrowIfRequestNull() {
        assertThrows(UserValidationException.class, () -> UserMapper.toEntity(null));
    }

    @Test
    void toEntity_shouldThrowIfRoleNull() {
        UserRequest req = new UserRequest("Test", "test@example.com", "password123", "+905551234567", null);
        assertThrows(UserValidationException.class, () -> UserMapper.toEntity(req));
    }

    @Test
    void toEntity_shouldThrowIfEmailNull() {
        UserRequest req = new UserRequest("Test", null, "password123", "+905551234567", User.Role.PATRON);
        assertThrows(UserValidationException.class, () -> UserMapper.toEntity(req));
    }

    @Test
    void toEntity_shouldThrowIfEmailInvalid() {
        UserRequest req = new UserRequest("Test", "invalid", "password123", "+905551234567", User.Role.PATRON);
        assertThrows(UserValidationException.class, () -> UserMapper.toEntity(req));
    }

    @Test
    void toDto_shouldMapAllFields() {
        User user = User.builder()
                .id(1L)
                .name("Test")
                .email("test@example.com")
                .phone("+905551234567")
                .role(User.Role.LIBRARIAN)
                .createdAt(LocalDateTime.now())
                .build();
        UserResponse dto = UserMapper.toDto(user);
        assertEquals(user.getId(), dto.id());
        assertEquals(user.getName(), dto.name());
        assertEquals(user.getEmail(), dto.email());
        assertEquals(user.getPhone(), dto.phone());
        assertEquals(user.getRole(), dto.role());
        assertEquals(user.getCreatedAt(), dto.createdAt());
    }

    @Test
    void toDto_shouldHandleNullFields() {
        User user = new User();
        UserResponse dto = UserMapper.toDto(user);
        assertNull(dto.id());
        assertNull(dto.name());
        assertNull(dto.email());
        assertNull(dto.phone());
        assertNull(dto.role());
        assertNull(dto.createdAt());
    }
} 