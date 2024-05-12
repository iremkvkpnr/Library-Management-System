package com.iremkvkpnr.librarymanagement.service;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookSearchRequest;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.exception.BookNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.mapper.BookMapper;

/**
 * Service layer for managing book operations.
 * Includes adding, updating, deleting, searching, and retrieving book details.
 */
@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Adds a new book.
     * @param request Book request DTO
     * @return Response DTO of the added book
     */
    @Transactional
    public BookResponse addBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            log.error("ISBN already exists: {}", request.isbn());
            throw new BookValidationException("ISBN already exists: " + request.isbn());
        }
        Book book = BookMapper.toEntity(request);
        Book saved = bookRepository.save(book);
        log.info("New book added: {}", saved);
        return BookMapper.toDto(saved);
    }

    /**
     * Retrieves book details by ID.
     * @param id Book ID
     * @return Book entity
     * @throws BookNotFoundException if book not found
     */
    @Transactional
    public Book getBookDetails(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found: id={}", id);
                    return new BookNotFoundException("Book not found with ID: " + id);
                });
        log.info("Book details retrieved: {}", book);
        return book;
    }

    /**
     * Searches books by criteria and returns paginated results.
     * @param request Search criteria
     * @param page Page number
     * @param size Page size
     * @return Page of books
     * @throws BookNotFoundException if no results found
     */
    @Transactional
    public Page<Book> searchBooks(BookSearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> result = bookRepository.searchBooks(
                request.title(),
                request.author(),
                request.isbn(),
                request.genre(),
                pageable
        );
        if (result.isEmpty()) {
            throw new BookNotFoundException("No books found for the given search criteria.");
        }
        return result;
    }

    /**
     * Updates book information.
     * @param id Book ID to update
     * @param request Updated information
     * @return Response DTO of the updated book
     * @throws BookNotFoundException if book not found
     * @throws BookValidationException if ISBN already exists
     */
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book to update not found: id={}", id);
                    return new BookNotFoundException("Book not found with ID: " + id);
                });

        if (request.title() != null && !request.title().trim().isEmpty()) {
            existingBook.setTitle(request.title());
        }
        if (request.author() != null && !request.author().trim().isEmpty()) {
            existingBook.setAuthor(request.author());
        }
        if (request.isbn() != null && !request.isbn().trim().isEmpty()) {
            if (!request.isbn().equals(existingBook.getIsbn()) && bookRepository.existsByIsbn(request.isbn())) {
                throw new BookValidationException("ISBN already exists");
            }
            existingBook.setIsbn(request.isbn());
        }
        if (request.totalCopies() > 0) {
            existingBook.setTotalCopies(request.totalCopies());
            existingBook.setAvailableCopies(request.totalCopies());
        }
        if (request.genre() != null) {
            existingBook.setGenre(Book.Genre.fromString(request.genre()));
        }
        if (request.publicationDate() != null) {
            existingBook.setPublicationDate(request.publicationDate());
        }
        Book updated = bookRepository.save(existingBook);
        log.info("Book updated: {}", updated);
        return BookMapper.toDto(updated);
    }

    /**
     * Deletes a book.
     * @param id Book ID to delete
     * @throws BookNotFoundException if book not found
     */
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book to delete not found: id={}", id);
                    return new BookNotFoundException("Book not found with ID: " + id);
                });
        bookRepository.delete(book);
        log.info("Book deleted: {}", book);
    }

}
