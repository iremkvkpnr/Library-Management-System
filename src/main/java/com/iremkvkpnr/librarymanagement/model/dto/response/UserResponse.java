package com.iremkvkpnr.librarymanagement.model.dto.response;

import com.iremkvkpnr.librarymanagement.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        User.Role role,
        boolean userEligibility,
        LocalDateTime createdAt
) { }

