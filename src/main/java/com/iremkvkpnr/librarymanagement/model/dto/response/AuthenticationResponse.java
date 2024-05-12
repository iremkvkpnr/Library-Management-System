package com.iremkvkpnr.librarymanagement.model.dto.response;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token
) {
}
