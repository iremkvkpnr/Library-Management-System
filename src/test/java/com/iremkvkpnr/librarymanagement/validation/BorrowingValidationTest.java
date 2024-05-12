package com.iremkvkpnr.librarymanagement.validation;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.BorrowingValidationException;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingValidationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    private BorrowingValidation borrowingValidation;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        borrowingValidation = new BorrowingValidation(userRepository, bookRepository);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setRole(User.Role.PATRON);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAvailableCopies(5);
        testBook.setTotalCopies(5);
    }

    @Test
    void validateBorrowing_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        assertDoesNotThrow(() -> borrowingValidation.validateBorrowing(1L, 1L));
        verify(userRepository).findById(1L);
        verify(bookRepository).findById(1L);
    }

    @Test
    void validateBorrowing_NullUserId() {
        assertThrows(BorrowingValidationException.class, 
            () -> borrowingValidation.validateBorrowing(null, 1L));
        verify(userRepository, never()).findById(any());
        verify(bookRepository, never()).findById(any());
    }

    @Test
    void validateBorrowing_NullBookId() {
        assertThrows(BorrowingValidationException.class, 
            () -> borrowingValidation.validateBorrowing(1L, null));
        verify(userRepository, never()).findById(any());
        verify(bookRepository, never()).findById(any());
    }

    @Test
    void validateBorrowing_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BorrowingValidationException.class, 
            () -> borrowingValidation.validateBorrowing(1L, 1L));
        verify(userRepository).findById(1L);
        verify(bookRepository, never()).findById(any());
    }

    @Test
    void validateBorrowing_LibrarianCannotBorrow() {
        testUser.setRole(User.Role.LIBRARIAN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(BorrowingValidationException.class, 
            () -> borrowingValidation.validateBorrowing(1L, 1L));
        verify(userRepository).findById(1L);
        verify(bookRepository, never()).findById(any());
    }

    @Test
    void validateBorrowing_BookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BorrowingValidationException.class, 
            () -> borrowingValidation.validateBorrowing(1L, 1L));
        verify(userRepository).findById(1L);
        verify(bookRepository).findById(1L);
    }

    @Test
    void validateBorrowing_BookNotAvailable() {
        testBook.setAvailableCopies(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        assertThrows(BorrowingValidationException.class, 
            () -> borrowingValidation.validateBorrowing(1L, 1L));
        verify(userRepository).findById(1L);
        verify(bookRepository).findById(1L);
    }

    @Test
    void validateBorrowing_BookWithNegativeAvailableCopies() {
        testBook.setAvailableCopies(-1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        assertThrows(BorrowingValidationException.class, 
            () -> borrowingValidation.validateBorrowing(1L, 1L));
        verify(userRepository).findById(1L);
        verify(bookRepository).findById(1L);
    }

    @Test
    void validateBorrowing_UserWithNullRole() {
        testUser.setRole(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(BorrowingValidationException.class, 
            () -> borrowingValidation.validateBorrowing(1L, 1L));
        verify(userRepository).findById(1L);
        verify(bookRepository, never()).findById(any());
    }
} 