package com.iremkvkpnr.librarymanagement.validation;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;
import org.springframework.stereotype.Component;

@Component
public class BookValidation {

    public void validateBookInput(BookRequest bookRequest) {
        if (bookRequest.title() == null || bookRequest.title().trim().isEmpty()) {
            throw new BookValidationException(BookValidationException.EMPTY_TITLE);
        }
        if (bookRequest.author() == null || bookRequest.author().trim().isEmpty()) {
            throw new BookValidationException(BookValidationException.EMPTY_AUTHOR);
        }
        if (bookRequest.isbn() == null || bookRequest.isbn().trim().isEmpty()) {
            throw new BookValidationException(BookValidationException.EMPTY_ISBN);
        }
        if (bookRequest.genre() == null || bookRequest.genre().trim().isEmpty()) {
            throw new BookValidationException(BookValidationException.EMPTY_GENRE);
        }
        if (bookRequest.publicationDate() == null) {
            throw new BookValidationException(BookValidationException.EMPTY_PUBLICATION_DATE);
        }
        if (bookRequest.totalCopies() < 0) {
            throw new BookValidationException("Total copies cannot be negative");
        }
    }
}
