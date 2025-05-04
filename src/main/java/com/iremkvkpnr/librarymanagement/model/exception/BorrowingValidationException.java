package com.iremkvkpnr.librarymanagement.model.exception;

public class BorrowingValidationException extends RuntimeException {

    public static final String EMPTY_USER_ID = "User ID cannot be null";
    public static final String EMPTY_BOOK_ID = "Book ID cannot be null";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_NOT_ELIGIBLE = "User is not eligible to borrow books";
    public static final String BOOK_NOT_FOUND = "Book not found";
    public static final String BOOK_NOT_AVAILABLE = "Book is not available for borrowing";
    public static final String BOOK_ALREADY_BORROWED = "Book already borrowed";

    public BorrowingValidationException(String message) {
        super(message);
    }
}
