package com.iremkvkpnr.librarymanagement;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookEntityTest {
    @Test
    void builderAndGettersShouldWork() {
        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .genre(Book.Genre.FICTION)
                .totalCopies(5)
                .availableCopies(5)
                .publicationDate(LocalDate.now())
                .build();

        assertEquals(1L, book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertEquals(Book.Genre.FICTION, book.getGenre());
        assertEquals(5, book.getTotalCopies());
        assertEquals(5, book.getAvailableCopies());
        assertNotNull(book.getPublicationDate());
    }

    @Test
    void genreFromStringShouldWork() {
        assertEquals(Book.Genre.FICTION, Book.Genre.fromString("fiction"));
        assertEquals(Book.Genre.HISTORY, Book.Genre.fromString("HISTORY"));
    }

    @Test
    void genreFromStringShouldThrowOnInvalid() {
        Exception ex = assertThrows(
                com.iremkvkpnr.librarymanagement.model.exception.BookValidationException.class,
                () -> Book.Genre.fromString("INVALID_GENRE")
        );
        assertTrue(ex.getMessage().contains("Invalid genre"));
    }

    @Test
    void prePersistShouldSetCreatedAt() throws Exception {
        Book book = new Book();
        java.lang.reflect.Method method = Book.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(book);
        assertNotNull(book.getCreatedAt());
        assertTrue(book.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
} 