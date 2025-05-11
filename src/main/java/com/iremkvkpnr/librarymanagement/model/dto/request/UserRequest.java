package com.iremkvkpnr.librarymanagement.model.dto.request;

import com.iremkvkpnr.librarymanagement.model.entity.User;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserRequest(
        @Schema(description = "User's full name")
        @NotBlank(message = "Name must not be blank")
        String name,

        @Schema(description = "User's email address")
        @Email(message = "Invalid e-posta format")
        @NotBlank(message = "E-posta must not be blank")
        String email,

        @Schema(description = "User's password")
        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, message = "Password must be at least 8 character")
        String password,

        @Schema(description = "User's phone number")
        @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Invalid phone number")
        String phone,

        @Schema(description = "User role (LIBRARIAN or PATRON)")
        @NotNull(message = "Rol must be choose")
        User.Role role
) { }
