package com.iremkvkpnr.librarymanagement.model.dto.request;
import jakarta.validation.constraints.NotNull;

public record BorrowingRequest(
        @NotNull(message = "User ID boş olamaz")
        Long userId,

        @NotNull(message = "User ID boş olamaz")
        Long bookId
) { }
