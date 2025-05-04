package com.iremkvkpnr.librarymanagement.controller;

import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.BorrowingRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.ReturnRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BookResponse;
import com.iremkvkpnr.librarymanagement.model.dto.response.BorrowingResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;
import com.iremkvkpnr.librarymanagement.model.exception.BorrowingNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.BorrowingValidationException;
import com.iremkvkpnr.librarymanagement.model.exception.UserPrincipalNotFoundException;
import com.iremkvkpnr.librarymanagement.model.mapper.BookMapper;
import com.iremkvkpnr.librarymanagement.model.mapper.BorrowingMapper;
import com.iremkvkpnr.librarymanagement.service.BorrowingService;
import com.iremkvkpnr.librarymanagement.validation.BorrowingValidation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {
    private final BorrowingService borrowingService;
    private final BorrowingValidation borrowingValidation;

    public BorrowingController(BorrowingService borrowingService, BorrowingValidation borrowingValidation) {
        this.borrowingService = borrowingService;
        this.borrowingValidation = borrowingValidation;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<BorrowingResponse> borrowBook(@Valid @RequestBody BorrowingRequest request) {
        try {
            System.out.println("AAAAUser ID: " + request.userId());
            borrowingValidation.validateBorrowing(request);
            Borrowing borrowing = borrowingService.borrowBook(request.userId(), request.bookId());
            return new ResponseEntity<>(BorrowingMapper.toDto(borrowing), HttpStatus.CREATED);
        } catch (BorrowingValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 - Validation hatası
        }
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnBook(@Valid @RequestBody ReturnRequest request) {
        try {
            borrowingService.returnBook(request.borrowingId());
            return ResponseEntity.ok().build(); // 200 OK, body yok
        } catch (BorrowingValidationException | BorrowingNotFoundException ex) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<BorrowingResponse>> getUserBorrowingHistory(@RequestParam Long userId) {
        try {
            List<Borrowing> borrowings = borrowingService.getUserBorrowingHistory(userId);
            List<BorrowingResponse> response = borrowings.stream()
                    .map(BorrowingMapper::toDto)
                    .toList();
            return ResponseEntity.ok(response);
        } catch (BorrowingValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    //@PreAuthorize("hasRole('LIBRARIAN')") //
    @GetMapping("/history/all")
    public ResponseEntity<List<BorrowingResponse>> getAllBorrowingHistory(@RequestParam Long librarianId) {
        try {
            List<Borrowing> borrowings = borrowingService.getAllBorrowingHistory(librarianId);
            List<BorrowingResponse> response = borrowings.stream()
                    .map(BorrowingMapper::toDto)
                    .toList();
            return ResponseEntity.ok(response);
        } catch (BorrowingValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/overdue-books")
    public ResponseEntity<List<BookResponse>> getOverdueBooks() {
        List<Borrowing> overdueBorrowings = borrowingService.getOverdueBooks();

        List<BookResponse> bookResponses = overdueBorrowings.stream()
                .map(borrowing -> BookMapper.toDto(borrowing.getBook()))
                .toList();

        return ResponseEntity.ok(bookResponses);
    }


    @GetMapping("/overdue-books/report")
    public ResponseEntity<String> getOverdueBooksReport(@RequestParam Long librarianId) {
        try {
            String report = borrowingService.generateOverdueBooksReport(librarianId);
            return ResponseEntity.ok(report);
        } catch (UserPrincipalNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Librarian not found.");
        } catch (BorrowingValidationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


}
