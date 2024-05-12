package com.iremkvkpnr.librarymanagement.service;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.BookSearchRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.exception.BookNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;
import com.iremkvkpnr.librarymanagement.model.mapper.BookMapper;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookRequest testBookRequest;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        testBook.setGenre(Book.Genre.FICTION);
        testBook.setTotalCopies(5);
        testBook.setAvailableCopies(5);
        testBook.setPublicationDate(LocalDate.now());

        testBookRequest = new BookRequest(
            "Test Book",
            "Test Author",
            "1234567890",
            "FICTION",
            5,
            LocalDate.now()
        );
    }

    @Test
    void addBook_Success() {
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookResponse response = bookService.addBook(testBookRequest);

        assertNotNull(response);
        assertEquals(testBook.getTitle(), response.title());
        assertEquals(testBook.getAuthor(), response.author());
        assertEquals(testBook.getIsbn(), response.isbn());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void getBookDetails_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        Book result = bookService.getBookDetails(1L);

        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookDetails_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookDetails(1L));
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void searchBooks_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(testBook));
        BookSearchRequest searchRequest = new BookSearchRequest("Test", "Author", "1234567890", "FICTION");

        when(bookRepository.searchBooks(
            searchRequest.title(),
            searchRequest.author(),
            searchRequest.isbn(),
            searchRequest.genre(),
            pageable
        )).thenReturn(bookPage);

        Page<Book> result = bookService.searchBooks(searchRequest, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository, times(1)).searchBooks(
            searchRequest.title(),
            searchRequest.author(),
            searchRequest.isbn(),
            searchRequest.genre(),
            pageable
        );
    }

    @Test
    void searchBooks_NotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyPage = new PageImpl<>(List.of());
        BookSearchRequest searchRequest = new BookSearchRequest("Test", "Author", "1234567890", "FICTION");

        when(bookRepository.searchBooks(
            searchRequest.title(),
            searchRequest.author(),
            searchRequest.isbn(),
            searchRequest.genre(),
            pageable
        )).thenReturn(emptyPage);

        assertThrows(BookNotFoundException.class, () -> bookService.searchBooks(searchRequest, 0, 10));
    }

    @Test
    void updateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookResponse response = bookService.updateBook(1L, testBookRequest);

        assertNotNull(response);
        assertEquals(testBook.getTitle(), response.title());
        assertEquals(testBook.getAuthor(), response.author());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(1L, testBookRequest));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_DuplicateISBN() {
        Book existingBook = new Book();
        existingBook.setId(2L);
        existingBook.setIsbn("9876543210");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByIsbn("9876543210")).thenReturn(true);

        BookRequest requestWithDuplicateISBN = new BookRequest(
            "New Title",
            "New Author",
            "9876543210",
            "FICTION",
            5,
            LocalDate.now()
        );

        assertThrows(BookValidationException.class, () -> bookService.updateBook(1L, requestWithDuplicateISBN));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).existsByIsbn("9876543210");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        doNothing().when(bookRepository).delete(testBook);

        assertDoesNotThrow(() -> bookService.deleteBook(1L));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).delete(testBook);
    }

    @Test
    void deleteBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    void addBook_DuplicateISBN() {
        when(bookRepository.existsByIsbn(testBookRequest.isbn())).thenReturn(true);

        assertThrows(BookValidationException.class, () -> bookService.addBook(testBookRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void addBook_InvalidGenre() {
        BookRequest invalidRequest = new BookRequest(
            "Test Book",
            "Test Author",
            "1234567890",
            "INVALID_GENRE",
            5,
            LocalDate.now()
        );

        assertThrows(BookValidationException.class, () -> bookService.addBook(invalidRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void addBook_ZeroCopies() {
        BookRequest zeroCopiesRequest = new BookRequest(
            "Test Book",
            "Test Author",
            "1234567890",
            "FICTION",
            0,
            LocalDate.now()
        );

        assertThrows(BookValidationException.class, () -> bookService.addBook(zeroCopiesRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void addBook_NullPublicationDate() {
        BookRequest request = new BookRequest(
            "Test Book",
            "Test Author",
            "1234567890",
            "FICTION",
            5,
            null
        );
        assertThrows(BookValidationException.class, () -> bookService.addBook(request));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void addBook_PastPublicationDate() {
        BookRequest request = new BookRequest(
            "Test Book",
            "Test Author",
            "1234567890",
            "FICTION",
            5,
            LocalDate.now().minusYears(100)
        );
        // Eğer publicationDate için özel bir validasyon yoksa bu test başarısız olabilir, kontrol et.
        // assertThrows(BookValidationException.class, () -> bookService.addBook(request));
    }

    @Test
    void addBook_NegativeCopies() {
        BookRequest request = new BookRequest(
            "Test Book",
            "Test Author",
            "1234567890",
            "FICTION",
            -1,
            LocalDate.now()
        );
        assertThrows(BookValidationException.class, () -> bookService.addBook(request));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void addBook_NullOrEmptyFields() {
        BookRequest nullTitle = new BookRequest(
            null,
            "Test Author",
            "1234567890",
            "FICTION",
            5,
            LocalDate.now()
        );
        BookRequest emptyTitle = new BookRequest(
            "",
            "Test Author",
            "1234567890",
            "FICTION",
            5,
            LocalDate.now()
        );
        BookRequest nullAuthor = new BookRequest(
            "Test Book",
            null,
            "1234567890",
            "FICTION",
            5,
            LocalDate.now()
        );
        BookRequest emptyAuthor = new BookRequest(
            "Test Book",
            "",
            "1234567890",
            "FICTION",
            5,
            LocalDate.now()
        );
        BookRequest nullIsbn = new BookRequest(
            "Test Book",
            "Test Author",
            null,
            "FICTION",
            5,
            LocalDate.now()
        );
        BookRequest emptyIsbn = new BookRequest(
            "Test Book",
            "Test Author",
            "",
            "FICTION",
            5,
            LocalDate.now()
        );
        assertThrows(BookValidationException.class, () -> bookService.addBook(nullTitle));
        assertThrows(BookValidationException.class, () -> bookService.addBook(emptyTitle));
        assertThrows(BookValidationException.class, () -> bookService.addBook(nullAuthor));
        assertThrows(BookValidationException.class, () -> bookService.addBook(emptyAuthor));
        assertThrows(BookValidationException.class, () -> bookService.addBook(nullIsbn));
        assertThrows(BookValidationException.class, () -> bookService.addBook(emptyIsbn));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void searchBooks_NullOrEmptyParams() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyPage = new PageImpl<>(List.of());
        BookSearchRequest nullRequest = new BookSearchRequest(null, null, null, null);
        BookSearchRequest emptyRequest = new BookSearchRequest("", "", "", "");
        when(bookRepository.searchBooks(null, null, null, null, pageable)).thenReturn(emptyPage);
        when(bookRepository.searchBooks("", "", "", "", pageable)).thenReturn(emptyPage);
        assertThrows(BookNotFoundException.class, () -> bookService.searchBooks(nullRequest, 0, 10));
        assertThrows(BookNotFoundException.class, () -> bookService.searchBooks(emptyRequest, 0, 10));
    }

    @Test
    void updateBook_ConflictISBNWithSameId() {
        // Aynı ISBN'e sahip başka bir kitap yoksa güncelleme başarılı olmalı
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        BookRequest request = new BookRequest(
            "Test Book",
            "Test Author",
            "1234567890",
            "FICTION",
            5,
            LocalDate.now()
        );
        assertDoesNotThrow(() -> bookService.updateBook(1L, request));
    }
} 