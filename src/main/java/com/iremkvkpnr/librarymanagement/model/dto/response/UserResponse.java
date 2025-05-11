package com.iremkvkpnr.librarymanagement.model.dto.response;

import com.iremkvkpnr.librarymanagement.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserResponse(
        @Schema(description = "User ID")
        Long id,
        @Schema(description = "User's full name")
        String name,
        @Schema(description = "User's email address")
        String email,
        @Schema(description = "User's phone number")
        String phone,
        @Schema(description = "User role (LIBRARIAN or PATRON)")
        User.Role role,
        LocalDateTime createdAt
) { }

