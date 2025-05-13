package com.iremkvkpnr.librarymanagement.security.service;

import com.iremkvkpnr.librarymanagement.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User testUser;
    private String secretKey;
    private long jwtExpiration;
    private long refreshExpiration;

    @BeforeEach
    void setUp() {
        secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        jwtExpiration = 86400000; // 24 saat
        refreshExpiration = 604800000; // 7 gün

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpiration);

        testUser = User.builder()
            .id(1L)
            .name("Test User")
            .email("test@example.com")
            .password("password123")
            .phone("1234567890")
            .role(User.Role.PATRON)
            .build();
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT formatı: header.payload.signature
    }

    @Test
    void generateToken_WithExtraClaims() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "PATRON");
        extraClaims.put("userId", 1L);

        String token = jwtService.generateToken(extraClaims, testUser);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void generateRefreshToken_Success() {
        String refreshToken = jwtService.generateRefreshToken(testUser);

        assertNotNull(refreshToken);
        assertTrue(refreshToken.split("\\.").length == 3);
    }

    @Test
    void extractUsername_Success() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);

        assertEquals(testUser.getEmail(), username);
    }

    @Test
    void extractClaim_Success() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractClaim(token, Claims::getSubject);

        assertEquals(testUser.getEmail(), username);
    }

    @Test
    void isTokenValid_Success() {
        String token = jwtService.generateToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testUser.getEmail());

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidUsername() {
        String token = jwtService.generateToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("wrong@email.com");

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        String expiredToken = jwtService.generateToken(testUser);
        
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(expiredToken, testUser);
        });
    }
} 