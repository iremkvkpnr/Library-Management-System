package com.iremkvkpnr.librarymanagement.service;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.BookSearchRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.exception.BookNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;
import com.iremkvkpnr.librarymanagement.model.mapper.BookMapper;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book getBookDetails(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
    }

    @Transactional
    public Page<Book> searchBooks(BookSearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.searchBooks(
                request.title(),
                request.author(),
                request.isbn(),
                request.genre(),
                pageable
        );
    }


    @Transactional
    public Book updateBook(Long id, Book book) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        if (book.getTitle() != null && !book.getTitle().trim().isEmpty()) {
            existingBook.setTitle(book.getTitle());
        }

        if (book.getAuthor() != null && !book.getAuthor().trim().isEmpty()) {
            existingBook.setAuthor(book.getAuthor());
        }

        if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
            // ISBN değiştirilmişse ve yeni ISBN zaten varsa hata fırlat
            if (!book.getIsbn().equals(existingBook.getIsbn()) && bookRepository.existsByIsbn(book.getIsbn())) {
                throw new BookValidationException("ISBN already exists");
            }
            existingBook.setIsbn(book.getIsbn());
        }
        if (book.getTotalCopies() > 0) {
            existingBook.setTotalCopies(book.getTotalCopies());
            existingBook.setAvailableCopies(book.getTotalCopies());
        }
        if (book.getGenre() != null) {
            existingBook.setGenre(book.getGenre()); // parseGenre kaldırıldı, doğrudan set ediliyor
        }

        if (book.getPublicationDate() != null) {
            existingBook.setPublicationDate(book.getPublicationDate());
        }

        return bookRepository.save(existingBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        bookRepository.delete(book);
    }

}
