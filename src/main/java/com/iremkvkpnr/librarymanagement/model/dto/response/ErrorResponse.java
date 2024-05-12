package com.iremkvkpnr.librarymanagement.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hata mesajı")
public record ErrorResponse(
    @Schema(description = "Hata mesajı", example = "Not found")
    String message
) {} 