package com.iremkvkpnr.librarymanagement.model.dto.request;

public record AuthenticationRequest (
        String email,
        String password
){
}
