package com.iremkvkpnr.librarymanagement.model.exception;

public class UserPrincipalNotFoundException extends RuntimeException {

    public UserPrincipalNotFoundException(String message) {
        super(message);
    }

    public UserPrincipalNotFoundException() {
        super("User not found");
    }
}
