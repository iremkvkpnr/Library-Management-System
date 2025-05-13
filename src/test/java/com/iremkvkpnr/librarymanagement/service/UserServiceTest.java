package com.iremkvkpnr.librarymanagement.service;

import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.UserResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.UserPrincipalNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.UserValidationException;
import com.iremkvkpnr.librarymanagement.repository.BorrowingRepository;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BorrowingRepository borrowingRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequest testUserRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setPhone("1234567890");
        testUser.setRole(User.Role.PATRON);

        testUserRequest = new UserRequest(
            "Test User",
            "test@example.com",
            "password123",
            "1234567890",
            User.Role.PATRON
        );
    }

    @Test
    void registerUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.registerUser(testUserRequest);

        assertNotNull(response);
        assertEquals(testUser.getName(), response.name());
        assertEquals(testUser.getEmail(), response.email());
        assertEquals(testUser.getPhone(), response.phone());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserDetails_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserDetails(1L);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals(testUser.getName(), response.name());
        assertEquals(testUser.getEmail(), response.email());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserDetails_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserPrincipalNotFoundException.class, () -> userService.getUserDetails(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUser(1L, testUserRequest);

        assertNotNull(response);
        assertEquals(testUser.getName(), response.name());
        assertEquals(testUser.getEmail(), response.email());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserPrincipalNotFoundException.class, () -> userService.updateUser(1L, testUserRequest));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserPrincipalNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void isUserEligible_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(borrowingRepository.findOverdueBooks(any(LocalDate.class))).thenReturn(Collections.emptyList());

        boolean result = userService.isUserEligible(1L);

        assertTrue(result);
        verify(userRepository, times(1)).findById(1L);
        verify(borrowingRepository, times(1)).findOverdueBooks(any(LocalDate.class));
    }

    @Test
    void isUserEligible_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserPrincipalNotFoundException.class, () -> userService.isUserEligible(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(borrowingRepository, never()).findOverdueBooks(any(LocalDate.class));
    }

    @Test
    void isUserEligible_HasOverdueBooks() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        Borrowing overdueBorrowing = new Borrowing();
        overdueBorrowing.setUser(testUser);
        when(borrowingRepository.findOverdueBooks(any(LocalDate.class))).thenReturn(List.of(overdueBorrowing));

        assertThrows(UserValidationException.class, () -> userService.isUserEligible(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(borrowingRepository, times(1)).findOverdueBooks(any(LocalDate.class));
    }

    @Test
    void registerUser_DuplicateEmail() {
        when(userRepository.existsByEmail(testUserRequest.email())).thenReturn(true);

        assertThrows(UserValidationException.class, () -> userService.registerUser(testUserRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_InvalidRole() {
        UserRequest invalidRequest = new UserRequest(
            "Test User",
            "test@example.com",
            "password123",
            "1234567890",
            null
        );

        assertThrows(UserValidationException.class, () -> userService.registerUser(invalidRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_InvalidEmail() {
        UserRequest invalidRequest = new UserRequest(
            "Test User",
            "invalid-email",
            "password123",
            "1234567890",
            User.Role.PATRON
        );

        assertThrows(UserValidationException.class, () -> userService.registerUser(invalidRequest));
        verify(userRepository, never()).save(any(User.class));
    }
} 