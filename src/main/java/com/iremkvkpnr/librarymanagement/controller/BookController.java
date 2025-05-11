package com.iremkvkpnr.librarymanagement.controller;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.BookSearchRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.dto.response.ErrorResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.mapper.BookMapper;
import com.iremkvkpnr.librarymanagement.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/books")
@PreAuthorize("hasAnyRole('LIBRARIAN', 'PATRON')")
@Tag(name = "Book Management", description = "Endpoints for managing books in the library.")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(
        summary = "Add a new book",
        description = "Librarians can add a new book to the library. Requires JWT token with LIBRARIAN role.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = BookRequest.class)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Validation error\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden\"}"))),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Not found\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Internal server error\"}")))
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.addBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Get book details",
        description = "Get detailed information about a book by its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book details returned successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookDetails(
        @Parameter(description = "ID of the book", example = "1") @PathVariable Long id) {
        Book book = bookService.getBookDetails(id);
        return ResponseEntity.ok(BookMapper.toDto(book));
    }

    @Operation(
        summary = "Search for books",
        description = "Search for books by title, author, ISBN, or genre. Pagination supported."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Books found and returned successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @Parameter(description = "Search criteria") @ModelAttribute BookSearchRequest request,
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        Page<Book> books = bookService.searchBooks(request, page, size);
        Page<BookResponse> response = books.map(BookMapper::toDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update book information",
        description = "Librarians can update book information. Requires JWT token with LIBRARIAN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book updated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<BookResponse> updateBook(
        @Parameter(description = "ID of the book to update", example = "1") @PathVariable Long id,
        @Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete a book",
        description = "Librarians can delete a book from the library. Requires JWT token with LIBRARIAN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Void> deleteBook(
        @Parameter(description = "ID of the book to delete", example = "1") @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
