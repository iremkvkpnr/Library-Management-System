package com.iremkvkpnr.librarymanagement.model.entity;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.mapper.BookMapper;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {
    @Test
    void toEntity_shouldMapAllFields() {
        BookRequest req = new BookRequest("Title", "Author", "1234567890", "FICTION", 5, LocalDate.now());
        Book book = BookMapper.toEntity(req);
        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertEquals(Book.Genre.FICTION, book.getGenre());
        assertEquals(5, book.getAvailableCopies());
        assertEquals(5, book.getTotalCopies());
        assertEquals(LocalDate.now(), book.getPublicationDate());
    }

    @Test
    void toEntity_shouldThrowIfRequestNull() {
        assertThrows(BookValidationException.class, () -> BookMapper.toEntity(null));
    }

    @Test
    void toEntity_shouldThrowIfTotalCopiesZeroOrNegative() {
        BookRequest req = new BookRequest("Title", "Author", "1234567890", "FICTION", 0, LocalDate.now());
        assertThrows(BookValidationException.class, () -> BookMapper.toEntity(req));
        BookRequest req2 = new BookRequest("Title", "Author", "1234567890", "FICTION", -1, LocalDate.now());
        assertThrows(BookValidationException.class, () -> BookMapper.toEntity(req2));
    }

    @Test
    void toEntity_shouldThrowIfGenreNullOrEmpty() {
        BookRequest req = new BookRequest("Title", "Author", "1234567890", null, 5, LocalDate.now());
        assertThrows(BookValidationException.class, () -> BookMapper.toEntity(req));
        BookRequest req2 = new BookRequest("Title", "Author", "1234567890", "", 5, LocalDate.now());
        assertThrows(BookValidationException.class, () -> BookMapper.toEntity(req2));
    }

    @Test
    void toEntity_shouldThrowIfGenreInvalid() {
        BookRequest req = new BookRequest("Title", "Author", "1234567890", "INVALID", 5, LocalDate.now());
        assertThrows(BookValidationException.class, () -> BookMapper.toEntity(req));
    }

    @Test
    void toDto_shouldMapAllFields() {
        Book book = Book.builder()
                .id(1L)
                .title("Title")
                .author("Author")
                .isbn("1234567890")
                .genre(Book.Genre.FICTION)
                .availableCopies(5)
                .totalCopies(5)
                .publicationDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();
        BookResponse dto = BookMapper.toDto(book);
        assertEquals(book.getId(), dto.id());
        assertEquals(book.getTitle(), dto.title());
        assertEquals(book.getAuthor(), dto.author());
        assertEquals(book.getIsbn(), dto.isbn());
        assertEquals(book.getGenre(), dto.genre());
        assertEquals(book.getAvailableCopies(), dto.availableCopies());
        assertEquals(book.getTotalCopies(), dto.totalCopies());
        assertEquals(book.getPublicationDate(), dto.publicationDate());
        assertEquals(book.getCreatedAt(), dto.createdAt());
    }

    @Test
    void toDto_shouldHandleNullFields() {
        Book book = new Book();
        BookResponse dto = BookMapper.toDto(book);
        assertNull(dto.id());
        assertNull(dto.title());
        assertNull(dto.author());
        assertNull(dto.isbn());
        assertNull(dto.genre());
        assertEquals(0, dto.availableCopies());
        assertEquals(0, dto.totalCopies());
        assertNull(dto.publicationDate());
        assertNull(dto.createdAt());
    }
} 