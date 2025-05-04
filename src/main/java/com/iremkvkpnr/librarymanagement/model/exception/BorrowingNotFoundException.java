package com.iremkvkpnr.librarymanagement.model.exception;

public class BorrowingNotFoundException extends RuntimeException {
    public BorrowingNotFoundException(String message) {
        super(message);
    }
}
