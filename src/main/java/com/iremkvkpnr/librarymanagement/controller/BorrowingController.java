package com.iremkvkpnr.librarymanagement.controller;

import com.iremkvkpnr.librarymanagement.model.dto.request.BorrowingRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.ReturnRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.dto.response.BorrowingResponse;
import com.iremkvkpnr.librarymanagement.model.dto.response.ErrorResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.mapper.BookMapper;
import com.iremkvkpnr.librarymanagement.model.mapper.BorrowingMapper;
import com.iremkvkpnr.librarymanagement.service.BorrowingService;
import com.iremkvkpnr.librarymanagement.security.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
@PreAuthorize("hasAnyRole('LIBRARIAN', 'PATRON')")
@Tag(name = "Borrowing Management", description = "Endpoints for borrowing and returning books.")
public class BorrowingController {
    private final BorrowingService borrowingService;
    private final JwtService jwtService;

    public BorrowingController(BorrowingService borrowingService, JwtService jwtService) {
        this.borrowingService = borrowingService;
        this.jwtService = jwtService;
    }

    @Operation(
        summary = "Borrow a book",
        description = "Patrons can borrow available books. Requires JWT token with PATRON role.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = BorrowingRequest.class)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book borrowed successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Validation error\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden\"}"))),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Not found\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"message\": \"Internal server error\"}")))
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PATRON')")
    public ResponseEntity<BorrowingResponse> borrowBook(@Valid @RequestBody BorrowingRequest request,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        BorrowingResponse response = borrowingService.borrowBook(userId, request.bookId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Return a book",
        description = "Patrons can return borrowed books. Requires JWT token with PATRON role.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = ReturnRequest.class)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book returned successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/return")
    @PreAuthorize("hasRole('PATRON')")
    public ResponseEntity<Void> returnBook(@Valid @RequestBody ReturnRequest request,
                                           @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        borrowingService.returnBook(userId, request.borrowingId());
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Get user borrowing history",
        description = "Get the borrowing history of a patron by their ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Borrowing history returned successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/history")
    public ResponseEntity<List<BorrowingResponse>> getUserBorrowingHistory(
        @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        List<Borrowing> borrowings = borrowingService.getUserBorrowingHistory(userId);
        List<BorrowingResponse> response = borrowings.stream()
                .map(BorrowingMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all borrowing history",
        description = "Librarians can get the borrowing history of all users. Requires JWT token with LIBRARIAN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All borrowing history returned successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/history/all")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<BorrowingResponse>> getAllBorrowingHistory(
        @Parameter(description = "ID of the librarian", example = "1") @RequestParam Long librarianId) {
        List<Borrowing> borrowings = borrowingService.getAllBorrowingHistory(librarianId);
        List<BorrowingResponse> response = borrowings.stream()
                .map(BorrowingMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get overdue books",
        description = "Get a list of all overdue books."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Overdue books returned successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/overdue-books")
    public ResponseEntity<List<BookResponse>> getOverdueBooks() {
        List<Borrowing> overdueBorrowings = borrowingService.getOverdueBooks();
        List<BookResponse> bookResponses = overdueBorrowings.stream()
                .map(borrowing -> BookMapper.toDto(borrowing.getBook()))
                .toList();
        return ResponseEntity.ok(bookResponses);
    }

    @Operation(
        summary = "Get overdue books report",
        description = "Librarians can generate a report for overdue books. Requires JWT token with LIBRARIAN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Overdue books report generated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/overdue-books/report")
    public ResponseEntity<String> getOverdueBooksReport(
        @Parameter(description = "ID of the librarian", example = "1") @RequestParam Long librarianId) {
        String report = borrowingService.generateOverdueBooksReport(librarianId);
        return ResponseEntity.ok(report);
    }
}
