package com.iremkvkpnr.librarymanagement.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BorrowingEntityTest {

    @Test
    @DisplayName("Builder should create borrowing with all fields")
    void builderShouldCreateBorrowingWithAllFields() {
        User user = User.builder().id(1L).build();
        Book book = Book.builder().id(2L).build();
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(7);
        
        Borrowing borrowing = Borrowing.builder()
                .id(3L)
                .user(user)
                .book(book)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .status(Borrowing.Status.BORROWED)
                .build();

        assertEquals(3L, borrowing.getId());
        assertEquals(user, borrowing.getUser());
        assertEquals(book, borrowing.getBook());
        assertEquals(borrowDate, borrowing.getBorrowDate());
        assertEquals(dueDate, borrowing.getDueDate());
        assertEquals(Borrowing.Status.BORROWED, borrowing.getStatus());
    }

    @Test
    @DisplayName("NoArgsConstructor should create empty borrowing")
    void noArgsConstructorShouldCreateEmptyBorrowing() {
        Borrowing borrowing = new Borrowing();
        assertNotNull(borrowing);
        assertNull(borrowing.getUser());
        assertNull(borrowing.getBook());
        assertNull(borrowing.getBorrowDate());
        assertNull(borrowing.getDueDate());
        assertNull(borrowing.getReturnDate());
        assertNull(borrowing.getStatus());
    }

    @Test
    @DisplayName("PrePersist should set createdAt")
    void prePersistShouldSetCreatedAt() throws Exception {
        Borrowing borrowing = new Borrowing();
        java.lang.reflect.Method method = Borrowing.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(borrowing);
        assertNotNull(borrowing.getCreatedAt());
        assertTrue(borrowing.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @ParameterizedTest
    @EnumSource(Borrowing.Status.class)
    @DisplayName("All statuses should be valid")
    void allStatusesShouldBeValid(Borrowing.Status status) {
        Borrowing borrowing = new Borrowing();
        borrowing.setStatus(status);
        assertEquals(status, borrowing.getStatus());
    }

    @Test
    @DisplayName("Borrowing should handle return date")
    void borrowingShouldHandleReturnDate() {
        Borrowing borrowing = new Borrowing();
        LocalDate returnDate = LocalDate.now();
        borrowing.setReturnDate(returnDate);
        assertEquals(returnDate, borrowing.getReturnDate());
    }

    @Test
    @DisplayName("Borrowing should handle null relationships")
    void borrowingShouldHandleNullRelationships() {
        Borrowing borrowing = new Borrowing();
        borrowing.setUser(null);
        borrowing.setBook(null);
        assertNull(borrowing.getUser());
        assertNull(borrowing.getBook());
    }

    @Test
    @DisplayName("Borrowing should handle null status")
    void borrowingShouldHandleNullStatus() {
        Borrowing borrowing = new Borrowing();
        borrowing.setStatus(null);
        assertNull(borrowing.getStatus());
    }

    @Test
    @DisplayName("Borrowing should handle null borrow date")
    void borrowingShouldHandleNullBorrowDate() {
        Borrowing borrowing = new Borrowing();
        borrowing.setBorrowDate(null);
        assertNull(borrowing.getBorrowDate());
    }

    @Test
    @DisplayName("Borrowing should handle null due date")
    void borrowingShouldHandleNullDueDate() {
        Borrowing borrowing = new Borrowing();
        borrowing.setDueDate(null);
        assertNull(borrowing.getDueDate());
    }

    @Test
    @DisplayName("Borrowing should handle null createdAt")
    void borrowingShouldHandleNullCreatedAt() {
        Borrowing borrowing = new Borrowing();
        borrowing.setCreatedAt(null);
        assertNull(borrowing.getCreatedAt());
    }

    @Test
    @DisplayName("Borrowing.Status enum valueOf ve toString tests")
    void borrowingStatusEnumValueOfAndToString() {
        for (Borrowing.Status status : Borrowing.Status.values()) {
            assertEquals(status, Borrowing.Status.valueOf(status.name()));
            assertNotNull(status.toString());
        }
    }

    @Test
    @DisplayName("Borrowing borrowDate and dueDate should null")
    void borrowingShouldAllowBothBorrowDateAndDueDateNull() {
        Borrowing borrowing = new Borrowing();
        borrowing.setBorrowDate(null);
        borrowing.setDueDate(null);
        assertNull(borrowing.getBorrowDate());
        assertNull(borrowing.getDueDate());
    }

    @Test
    @DisplayName("Borrowing.Status valueOf null and blank string should throw exception ")
    void borrowingStatusValueOfShouldThrowExceptionForNullOrEmpty() {
        assertThrows(NullPointerException.class, () -> Borrowing.Status.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> Borrowing.Status.valueOf(""));
    }
} 