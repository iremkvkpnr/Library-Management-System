package com.iremkvkpnr.librarymanagement.model.mapper;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;

public class BookMapper {
    public static Book toEntity(BookRequest bookRequest) {
        if(bookRequest==null) {
            throw new BookValidationException("Book request cannot be null");
        }
        if(bookRequest.genre() == null || bookRequest.genre().trim().isEmpty()) {
            throw new BookValidationException("Genre must be specified");
        }
        if(bookRequest.title() == null || bookRequest.title().trim().isEmpty()) {
            throw new BookValidationException("Title must be specified");
        }
        if(bookRequest.author() == null || bookRequest.author().trim().isEmpty()) {
            throw new BookValidationException("Author must be specified");
        }
        if(bookRequest.isbn() == null || bookRequest.isbn().trim().isEmpty()) {
            throw new BookValidationException("ISBN must be specified");
        }
        if(bookRequest.publicationDate() == null) {
            throw new BookValidationException("Publication date must be specified");
        }
        return Book.builder()
                .title(bookRequest.title())
                .author(bookRequest.author())
                .isbn(bookRequest.isbn())
                .genre(Book.Genre.fromString(bookRequest.genre()))
                .publicationDate(bookRequest.publicationDate())
                .availableCopies(bookRequest.totalCopies())
                .totalCopies(bookRequest.totalCopies())
                .build();
    }

    public static BookResponse toDto(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationDate(),
                book.getGenre(),
                book.getAvailableCopies(),
                book.getTotalCopies(),
                book.getCreatedAt()
        );
    }
}


