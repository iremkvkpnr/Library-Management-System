package com.iremkvkpnr.librarymanagement.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReturnRequest(
        @NotNull(message = "Borrowing ID must not be null")
        Long borrowingId
) {
}
