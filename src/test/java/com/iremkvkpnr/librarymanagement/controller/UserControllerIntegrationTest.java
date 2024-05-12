package com.iremkvkpnr.librarymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import com.iremkvkpnr.librarymanagement.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String userToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Admin kullanıcısı oluştur
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.LIBRARIAN);
        admin.setName("Admin User");
        admin.setPhone("1234567890");
        userRepository.save(admin);

        // Normal kullanıcı oluştur
        testUser = new User();
        testUser.setEmail("user@test.com");
        testUser.setPassword(passwordEncoder.encode("user123"));
        testUser.setRole(User.Role.PATRON);
        testUser.setName("Test User");
        testUser.setPhone("9876543210");
        userRepository.save(testUser);

        // Token'ları oluştur
        adminToken = "Bearer " + jwtService.generateToken(admin);
        userToken = "Bearer " + jwtService.generateToken(testUser);
    }

    @Test
    void getUserDetails_ValidId_ReturnsUserDetails() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getUserDetails_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ValidRequest_ReturnsUpdatedUser() throws Exception {
        UserRequest request = new UserRequest(
            "Updated User",
            "user@test.com",
            "newpass123",
            "5555555555",
            User.Role.PATRON
        );

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    void deleteUser_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/999")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserDetails_WithUserRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    public void getAllUsers_WithLibrarianRole_ReturnsOk() throws Exception {
        // ... existing code ...
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    public void getUserById_WithLibrarianRole_ReturnsOk() throws Exception {
        // ... existing code ...
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    public void updateUser_WithLibrarianRole_ReturnsOk() throws Exception {
        // ... existing code ...
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    public void deleteUser_WithLibrarianRole_ReturnsNoContent() throws Exception {
        // ... existing code ...
    }
} 