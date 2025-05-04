package com.iremkvkpnr.librarymanagement.model.dto.request;

import com.iremkvkpnr.librarymanagement.model.entity.User;
import jakarta.validation.constraints.*;

public record UserRequest(
        @NotBlank(message = "Ad alanı boş olamaz")
        String name,

        @Email(message = "Geçersiz e-posta formatı")
        @NotBlank(message = "E-posta boş olamaz")
        String email,

        @NotBlank(message = "Şifre boş olamaz")
        @Size(min = 8, message = "Şifre en az 8 karakter olmalı")
        String password,

        @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Geçersiz telefon numarası")
        String phone,

        @NotNull(message = "Rol seçilmelidir")
        User.Role role,

        boolean userEligibility
) { }
