package com.iremkvkpnr.librarymanagement.model.exception;

public class UserValidationException extends RuntimeException {

    // Sabit hata mesajları
    public static final String EMPTY_NAME = "User name cannot be empty";
    public static final String EMPTY_EMAIL = "User email cannot be empty";
    public static final String INVALID_EMAIL_FORMAT = "User email format is invalid";
    public static final String EMPTY_PHONE = "User phone number cannot be empty";
    public static final String USER_NOT_ELIGIBLE = "User is not eligible for borrowing";

    public UserValidationException(String message) {
        super(message);
    }
}
