package com.iremkvkpnr.librarymanagement.security.service;

import com.iremkvkpnr.librarymanagement.model.dto.request.AuthenticationRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.RegisterRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.AuthenticationResponse;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.UserValidationException;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service layer for authentication and registration operations.
 * Handles user registration and authentication, and issues JWT tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user and returns a JWT token.
     * @param request Registration request DTO
     * @return Authentication response with JWT token
     */
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserValidationException("Email already exists: " + request.email());
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(User.Role.PATRON)
                .build();
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Authenticates a user and returns a JWT token if credentials are valid.
     * @param request Authentication request DTO
     * @return Authentication response with JWT token
     * @throws UserValidationException if credentials are invalid
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (Exception e) {
            throw new UserValidationException("Invalid email or password");
        }
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserValidationException("User not found"));
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
} 