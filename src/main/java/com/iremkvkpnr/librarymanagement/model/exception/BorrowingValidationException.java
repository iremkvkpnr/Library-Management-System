package com.iremkvkpnr.librarymanagement.model.exception;

public class BorrowingValidationException extends RuntimeException {

    public static final String EMPTY_USER_ID = "User ID cannot be null";
    public static final String EMPTY_BOOK_ID = "Book ID cannot be null";
    public static final String BOOK_NOT_AVAILABLE = "Book is not available for borrowing";

    public BorrowingValidationException(String message) {
        super(message);
    }
}
