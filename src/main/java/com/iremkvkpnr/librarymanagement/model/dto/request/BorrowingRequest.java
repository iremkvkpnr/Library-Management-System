package com.iremkvkpnr.librarymanagement.model.dto.request;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record BorrowingRequest(
        @Schema(description = "ID of the book to be borrowed")
        @NotNull(message = "Book ID must not be null")
        Long bookId
) { }
