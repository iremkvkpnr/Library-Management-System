package com.iremkvkpnr.librarymanagement.service;

import com.iremkvkpnr.librarymanagement.model.dto.response.BorrowingResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.BorrowingValidationException;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import com.iremkvkpnr.librarymanagement.repository.BorrowingRepository;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import com.iremkvkpnr.librarymanagement.validation.BorrowingValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserService userService;

    @Mock
    private BorrowingValidation borrowingValidation;

    @InjectMocks
    private BorrowingService borrowingService;

    private User testUser;
    private Book testBook;
    private Borrowing testBorrowing;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setRole(User.Role.PATRON);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAvailableCopies(5);
        testBook.setTotalCopies(5);

        testBorrowing = Borrowing.builder()
                .id(1L)
                .user(testUser)
                .book(testBook)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusWeeks(2))
                .status(Borrowing.Status.BORROWED)
                .build();
    }

    @Test
    void borrowBook_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userService.isUserEligible(1L)).thenReturn(true);
        when(borrowingRepository.countOverdueBooksByUser(1L)).thenReturn(0L);
        when(borrowingRepository.countActiveBorrowingsByUser(1L)).thenReturn(0L);
        when(borrowingRepository.findActiveBorrowingByUserAndBook(1L, 1L)).thenReturn(Optional.empty());
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(testBorrowing);

        BorrowingResponse response = borrowingService.borrowBook(1L, 1L);

        assertNotNull(response);
        assertEquals(testBorrowing.getId(), response.id());
        assertEquals(testBook.getTitle(), response.bookTitle());
        assertEquals(testUser.getName(), response.userName());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(borrowingRepository, times(1)).save(any(Borrowing.class));
    }

    @Test
    void borrowBook_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BorrowingValidationException.class, () -> borrowingService.borrowBook(1L, 1L));
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    void borrowBook_BookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userService.isUserEligible(1L)).thenReturn(true);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BorrowingValidationException.class, () -> borrowingService.borrowBook(1L, 1L));
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    void borrowBook_NoAvailableCopies() {
        testBook.setAvailableCopies(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userService.isUserEligible(1L)).thenReturn(true);

        assertThrows(BorrowingValidationException.class, () -> borrowingService.borrowBook(1L, 1L));
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    void returnBook_Success() {
        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(testBorrowing));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(testBorrowing);

        assertDoesNotThrow(() -> borrowingService.returnBook(1L, 1L));
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(borrowingRepository, times(1)).save(any(Borrowing.class));
    }

    @Test
    void returnBook_BorrowingNotFound() {
        when(borrowingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BorrowingValidationException.class, () -> borrowingService.returnBook(1L, 1L));
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    void returnBook_UnauthorizedUser() {
        testBorrowing.getUser().setId(2L);
        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(testBorrowing));

        assertThrows(BorrowingValidationException.class, () -> borrowingService.returnBook(1L, 1L));
        verify(bookRepository, never()).save(any(Book.class));
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    void getUserBorrowingHistory_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(borrowingRepository.findByUserId(1L)).thenReturn(List.of(testBorrowing));

        List<Borrowing> result = borrowingService.getUserBorrowingHistory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBorrowing.getId(), result.get(0).getId());
    }

    @Test
    void getUserBorrowingHistory_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BorrowingValidationException.class, () -> borrowingService.getUserBorrowingHistory(1L));
    }

    @Test
    void getAllBorrowingHistory_Success() {
        User librarian = new User();
        librarian.setId(1L);
        librarian.setRole(User.Role.LIBRARIAN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(librarian));
        when(borrowingRepository.findAll()).thenReturn(List.of(testBorrowing));

        List<Borrowing> result = borrowingService.getAllBorrowingHistory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBorrowing.getId(), result.get(0).getId());
    }

    @Test
    void getAllBorrowingHistory_NotLibrarian() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(BorrowingValidationException.class, () -> borrowingService.getAllBorrowingHistory(1L));
    }

    @Test
    void getOverdueBooks_Success() {
        when(borrowingRepository.findOverdueBooks(any(LocalDate.class))).thenReturn(List.of(testBorrowing));

        List<Borrowing> result = borrowingService.getOverdueBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBorrowing.getId(), result.get(0).getId());
    }

    @Test
    void generateOverdueBooksReport_Success() {
        User librarian = new User();
        librarian.setId(1L);
        librarian.setRole(User.Role.LIBRARIAN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(librarian));
        when(borrowingRepository.findOverdueBooks(any(LocalDate.class))).thenReturn(List.of(testBorrowing));

        String report = borrowingService.generateOverdueBooksReport(1L);

        assertNotNull(report);
        assertTrue(report.contains("OVERDUE BOOKS REPORT"));
        assertTrue(report.contains(testBook.getTitle()));
        assertTrue(report.contains(testUser.getName()));
    }

    @Test
    void generateOverdueBooksReport_NotLibrarian() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(BorrowingValidationException.class, () -> borrowingService.generateOverdueBooksReport(1L));
    }

    @Test
    void generateOverdueBooksReport_NoOverdueBooks() {
        User librarian = new User();
        librarian.setId(1L);
        librarian.setRole(User.Role.LIBRARIAN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(librarian));
        when(borrowingRepository.findOverdueBooks(any(LocalDate.class))).thenReturn(Collections.emptyList());

        String report = borrowingService.generateOverdueBooksReport(1L);

        assertNotNull(report);
        assertEquals("No overdue books currently.", report);
    }
} 