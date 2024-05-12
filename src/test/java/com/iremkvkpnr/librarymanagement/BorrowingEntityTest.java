package com.iremkvkpnr.librarymanagement;

import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BorrowingEntityTest {
    @Test
    void builderAndGettersShouldWork() {
        User user = User.builder().id(1L).build();
        Book book = Book.builder().id(2L).build();
        Borrowing borrowing = Borrowing.builder()
                .id(3L)
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .status(Borrowing.Status.BORROWED)
                .build();

        assertEquals(3L, borrowing.getId());
        assertEquals(user, borrowing.getUser());
        assertEquals(book, borrowing.getBook());
        assertEquals(Borrowing.Status.BORROWED, borrowing.getStatus());
        assertNotNull(borrowing.getBorrowDate());
        assertNotNull(borrowing.getDueDate());
    }

    @Test
    void statusEnumShouldWork() {
        assertEquals(Borrowing.Status.BORROWED, Borrowing.Status.valueOf("BORROWED"));
        assertEquals(Borrowing.Status.RETURNED, Borrowing.Status.valueOf("RETURNED"));
        assertEquals(Borrowing.Status.PENDING, Borrowing.Status.valueOf("PENDING"));
    }

    @Test
    void prePersistShouldSetCreatedAt() throws Exception {
        Borrowing borrowing = new Borrowing();
        java.lang.reflect.Method method = Borrowing.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(borrowing);
        assertNotNull(borrowing.getCreatedAt());
        assertTrue(borrowing.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
} 