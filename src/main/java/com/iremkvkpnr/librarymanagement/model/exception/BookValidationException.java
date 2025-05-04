package com.iremkvkpnr.librarymanagement.model.exception;

public class BookValidationException extends RuntimeException{
    public static final String EMPTY_TITLE = "Book title cannot be empty";
    public static final String EMPTY_AUTHOR = "Book author cannot be empty";
    public static final String EMPTY_ISBN = "ISBN cannot be empty";
    public static final String EMPTY_GENRE = "Genre must be specified";
    public static final String EMPTY_PUBLICATION_DATE = "Publication date must be provided";

    public BookValidationException(String message) {
        super(message);
    }
}
