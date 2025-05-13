package com.iremkvkpnr.librarymanagement.service;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.BorrowingValidationException;
import com.iremkvkpnr.librarymanagement.model.exception.UserPrincipalNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.UserValidationException;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import com.iremkvkpnr.librarymanagement.repository.BorrowingRepository;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import com.iremkvkpnr.librarymanagement.validation.BorrowingValidation;
import com.iremkvkpnr.librarymanagement.model.dto.response.BorrowingResponse;
import com.iremkvkpnr.librarymanagement.model.mapper.BorrowingMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service layer for borrowing operations.
 * Handles borrowing, returning, history, and overdue book management.
 */
@Service
public class BorrowingService {

    private static final Logger log = LoggerFactory.getLogger(BorrowingService.class);

    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final BorrowingValidation borrowingValidation;

    public BorrowingService(BorrowingRepository borrowingRepository, UserRepository userRepository, BookRepository bookRepository, UserService userService, BorrowingValidation borrowingValidation) {
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.borrowingValidation = borrowingValidation;
    }

    /**
     * Allows a user to borrow a book if all business rules are satisfied.
     * @param userId ID of the user borrowing the book
     * @param bookId ID of the book to be borrowed
     * @return Borrowing response DTO
     * @throws BorrowingValidationException if any business rule is violated
     */
    @Transactional
    public BorrowingResponse borrowBook(Long userId, Long bookId) {
        borrowingValidation.validateBorrowing(userId, bookId);
        // Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BorrowingValidationException("User not found with ID: " + userId));
        // Check if user is eligible
        try {
            if (!userService.isUserEligible(userId)) {
                throw new UserValidationException("User is not eligible to borrow books.");
            }
        } catch (UserValidationException | UserPrincipalNotFoundException e) {
            throw e;
        }
        // Check for overdue books
        if (borrowingRepository.countOverdueBooksByUser(userId) > 0) {
            throw new BorrowingValidationException("You have overdue books. Please return them before borrowing new books.");
        }
        // Check active borrowing count (maximum 3 books)
        if (borrowingRepository.countActiveBorrowingsByUser(userId) >= 3) {
            throw new BorrowingValidationException("You have reached the maximum limit of 3 active borrowings.");
        }
        // Find the book and check availability
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BorrowingValidationException("Book not found with ID: " + bookId));
        if (book.getAvailableCopies() <= 0) {
            throw new BorrowingValidationException("No available copies for this book");
        }
        // Check if user has already borrowed this book
        if (borrowingRepository.findActiveBorrowingByUserAndBook(userId, bookId).isPresent()) {
            throw new BorrowingValidationException("You have already borrowed this book and haven't returned it yet.");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        // Create borrowing record
        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusWeeks(2))
                .status(Borrowing.Status.BORROWED)
                .build();
        Borrowing saved = borrowingRepository.save(borrowing);
        log.info("Book borrowed: userId={}, bookId={}, borrowingId={}", userId, bookId, saved.getId());
        return BorrowingMapper.toDto(saved);
    }

    /**
     * Allows a user to return a borrowed book.
     * @param userId ID of the user returning the book
     * @param borrowingId ID of the borrowing record
     * @throws BorrowingValidationException if the user is not authorized or record not found
     */
    @Transactional
    public Borrowing returnBook(Long userId, Long borrowingId) {
        // Find the borrowing record
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new BorrowingValidationException("Borrowing not found with ID: " + borrowingId));
        // Check user authorization
        if (!borrowing.getUser().getId().equals(userId)) {
            throw new BorrowingValidationException("You are not authorized to return this book.");
        }
        // Return the book
        borrowing.setStatus(Borrowing.Status.RETURNED);
        borrowing.setReturnDate(LocalDate.now());
        Book book = borrowing.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return borrowingRepository.save(borrowing);
    }

    /**
     * Retrieves the borrowing history of a user.
     * @param userId ID of the user
     * @return List of borrowings
     */
    public List<Borrowing> getUserBorrowingHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BorrowingValidationException("User not found"));
        List<Borrowing> result = borrowingRepository.findByUserId(userId);
        log.info("User borrowing history retrieved: userId={}, recordCount={}", userId, result.size());
        return result;
    }

    /**
     * Retrieves the borrowing history for all users (librarian only).
     * @param librarianId ID of the librarian
     * @return List of all borrowings
     * @throws BorrowingValidationException if the user is not a librarian
     */
    public List<Borrowing> getAllBorrowingHistory(Long librarianId) {
        // Check if user is a librarian
        User librarian = userRepository.findById(librarianId)
                .orElseThrow(() -> new UserPrincipalNotFoundException("User not found"));

        if (librarian.getRole() != User.Role.LIBRARIAN) {
            throw new BorrowingValidationException("Only librarians can view all borrowing history");
        }

        List<Borrowing> result = borrowingRepository.findAll();
        log.info("All borrowing history retrieved: librarianId={}, recordCount={}", librarianId, result.size());
        return result;
    }

    /**
     * Retrieves all overdue borrowings.
     * @return List of overdue borrowings
     */
    public List<Borrowing> getOverdueBooks() {
        LocalDate currentDate = LocalDate.now();
        List<Borrowing> result = borrowingRepository.findOverdueBooks(currentDate);
        log.info("Overdue books retrieved: count={}", result.size());
        return result;
    }

    /**
     * Generates a report of all overdue books (librarian only).
     * @param librarianId ID of the librarian
     * @return String report of overdue books
     * @throws BorrowingValidationException if the user is not a librarian
     */
    public String generateOverdueBooksReport(Long librarianId) {
        User librarian = userRepository.findById(librarianId)
                .orElseThrow(() -> new UserPrincipalNotFoundException("Librarian not found"));

        if (librarian.getRole() != User.Role.LIBRARIAN) {
            throw new BorrowingValidationException("Only librarians can generate overdue book reports");
        }

        List<Borrowing> overdueBooks = getOverdueBooks();
        if (overdueBooks.isEmpty()) {
            return "No overdue books currently.";
        }

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("OVERDUE BOOKS REPORT\n")
                .append("----------------------\n")
                .append(String.format("Total Overdue Books: %d\n", overdueBooks.size()))
                .append("\n");

        reportBuilder.append("Book Title | User Name | Borrow Date | Due Date | Overdue Days | Status\n")
                .append("------------------------------------------------------------\n");

        for (Borrowing borrowing : overdueBooks) {
            long overdueDays = ChronoUnit.DAYS.between(borrowing.getDueDate(), LocalDate.now());
            reportBuilder.append(String.format("%s | %s | %s | %s | %d | %s\n",
                    borrowing.getBook().getTitle(),
                    borrowing.getUser().getName(),
                    borrowing.getBorrowDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    borrowing.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    overdueDays,
                    borrowing.getStatus()));
        }

        return reportBuilder.toString();
    }
}
