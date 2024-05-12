package com.iremkvkpnr.librarymanagement.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name must not be blank")
        String name,
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,
        @NotBlank(message = "Phone must not be blank")
        @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
        String phone
) {
}


