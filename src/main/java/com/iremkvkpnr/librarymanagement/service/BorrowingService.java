package com.iremkvkpnr.librarymanagement.service;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.BookNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.BorrowingNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.BorrowingValidationException;
import com.iremkvkpnr.librarymanagement.model.exception.UserPrincipalNotFoundException;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import com.iremkvkpnr.librarymanagement.repository.BorrowingRepository;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BorrowingService(BorrowingRepository borrowingRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Borrowing borrowBook(Long userId, Long bookId) {
        // Step 1: Validate user eligibility
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BorrowingValidationException("User not found"));

        //if (user==patron) ->true not false
        if (!user.isUserEligibility()) {
            throw new BorrowingValidationException("User is not eligible to borrow books");
        }

        // Step 2: Validate book availability
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BorrowingValidationException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new BorrowingValidationException("No available copies for this book");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        // Step 3: Create borrowing record
        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusWeeks(2))  // Due date set to 2 weeks from today
                .status(Borrowing.Status.BORROWED)  // Initially status is BORROWED
                .build();

        // Save the borrowing record
        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public void returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new BorrowingNotFoundException("Borrowing record not found"));

        Book book = borrowing.getBook();
        User user = borrowing.getUser();

        if (user.getRole() != User.Role.PATRON) {
            throw new BorrowingValidationException("Only patrons can return books");
        }

        borrowing.setStatus(Borrowing.Status.RETURNED);
        borrowing.setReturnDate(LocalDate.now());
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        borrowingRepository.save(borrowing);
        bookRepository.save(book);
    }

    //user can see own history
    public List<Borrowing> getUserBorrowingHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BorrowingValidationException("User not found"));

        return borrowingRepository.findByUserId(userId);
    }


    //librarian can see all history
    public List<Borrowing> getAllBorrowingHistory(Long librarianId) {
        // check Librarian
        User librarian = userRepository.findById(librarianId)
                .orElseThrow(() -> new UserPrincipalNotFoundException("User not found"));

        if (librarian.getRole() != User.Role.LIBRARIAN) {
            throw new BorrowingValidationException("Only librarians can view all borrowing history");
        }

        return borrowingRepository.findAll();
    }

    public List<Borrowing> getOverdueBooks() {
        LocalDate currentDate = LocalDate.now();

        /* return borrowingRepository.findAll().stream()
                .filter(borrowing -> borrowing.getDueDate().isBefore(currentDate) && borrowing.getReturnDate() == null)
                .collect(Collectors.toList()); */
        return borrowingRepository.findOverdueBooks(currentDate);
    }

    //overdue generated report
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
            String bookTitle = borrowing.getBook().getTitle();
            String userName = borrowing.getUser().getName();
            LocalDate borrowDate = borrowing.getBorrowDate();
            LocalDate dueDate = borrowing.getDueDate();
            long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            String status = borrowing.getStatus().toString();

            reportBuilder.append(String.format("%s | %s | %s | %s | %d days | %s\n",
                    bookTitle,
                    userName,
                    borrowDate,
                    dueDate,
                    overdueDays,
                    status));
        }

        reportBuilder.append("\nLast Updated: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return reportBuilder.toString();
    }

}
