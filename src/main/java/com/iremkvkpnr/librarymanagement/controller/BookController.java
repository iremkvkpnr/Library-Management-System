package com.iremkvkpnr.librarymanagement.controller;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.BookSearchRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.exception.BookNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;
import com.iremkvkpnr.librarymanagement.model.mapper.BookMapper;
import com.iremkvkpnr.librarymanagement.model.mapper.UserMapper;
import com.iremkvkpnr.librarymanagement.service.BookService;
import com.iremkvkpnr.librarymanagement.validation.BookValidation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/books")
// for swagger @Tag(name = "Library Management", description = "API endpoints for task CRUD operations")
public class BookController {

    private final BookService bookService;
    private final BookValidation bookValidation;

    public BookController(BookService bookService, BookValidation bookValidation) {
        this.bookService = bookService;
        this.bookValidation = bookValidation;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody BookRequest request) {
        try {
            bookValidation.validateBookInput(request);
            Book book=bookService.addBook(BookMapper.toEntity(request));
            return new ResponseEntity<>(BookMapper.toDto(book), HttpStatus.CREATED);
        } catch (BookValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return 400 if validation fails
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookDetails(@PathVariable Long id) {
        try {
            Book book = bookService.getBookDetails(id);
            return ResponseEntity.ok(BookMapper.toDto(book));
        } catch (BookNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if book not found
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @ModelAttribute BookSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Book> books = bookService.searchBooks(request, page, size);
        Page<BookResponse> response = books.map(BookMapper::toDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id,@Valid @RequestBody BookRequest request) {
        try {
            bookValidation.validateBookInput(request);
            Book updatedBook = bookService.updateBook(id,BookMapper.toEntity(request));
            return ResponseEntity.ok(BookMapper.toDto(updatedBook)); // Return status 200 with updated book details
        } catch (BookNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return status 404 if book not found
        } catch (BookValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return status 400 if validation fails
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build(); // Return 204 if successful deletion
        } catch (BookNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if book not found
        }
    }
}
