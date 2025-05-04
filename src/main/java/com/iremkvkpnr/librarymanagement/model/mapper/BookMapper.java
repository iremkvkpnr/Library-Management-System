package com.iremkvkpnr.librarymanagement.model.mapper;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;

import static com.iremkvkpnr.librarymanagement.utils.BookUtils.parseGenre;

public class BookMapper {
    public static Book toEntity(BookRequest bookRequest) {
        if(bookRequest==null) {
            return null;
        }
        return Book.builder()
                .title(bookRequest.title())
                .author(bookRequest.author())
                .isbn(bookRequest.isbn())
                .genre(parseGenre(bookRequest.genre()))
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


