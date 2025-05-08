package com.iremkvkpnr.librarymanagement.security.controller;

import com.iremkvkpnr.librarymanagement.model.dto.request.AuthenticationRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.RegisterRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.AuthenticationResponse;
import com.iremkvkpnr.librarymanagement.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user and return JWT token")
    public ResponseEntity<AuthenticationResponse> register(
            @jakarta.validation.Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate a user", description = "Authenticate a user and return JWT token")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
} 