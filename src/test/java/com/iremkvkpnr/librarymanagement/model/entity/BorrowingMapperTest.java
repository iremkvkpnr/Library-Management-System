package com.iremkvkpnr.librarymanagement.model.entity;

import com.iremkvkpnr.librarymanagement.model.dto.request.BorrowingRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BorrowingResponse;
import com.iremkvkpnr.librarymanagement.model.mapper.BorrowingMapper;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class BorrowingMapperTest {
    @Test
    void toEntity_shouldMapAllFields() {
        User user = User.builder().id(1L).name("Ali").build();
        Book book = Book.builder().id(2L).title("Kitap").author("Yazar").build();
        BorrowingRequest req = new BorrowingRequest(2L);
        Borrowing borrowing = BorrowingMapper.toEntity(req, user, book);
        assertEquals(user, borrowing.getUser());
        assertEquals(book, borrowing.getBook());
        assertEquals(LocalDate.now(), borrowing.getBorrowDate());
        assertEquals(LocalDate.now().plusDays(14), borrowing.getDueDate());
        assertEquals(Borrowing.Status.PENDING, borrowing.getStatus());
    }

    @Test
    void toDto_shouldMapAllFields() {
        User user = User.builder().id(1L).name("Ali").build();
        Book book = Book.builder().id(2L).title("Kitap").author("Yazar").build();
        Borrowing borrowing = Borrowing.builder()
                .id(3L)
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .returnDate(LocalDate.now().plusDays(10))
                .status(Borrowing.Status.BORROWED)
                .build();
        BorrowingResponse dto = BorrowingMapper.toDto(borrowing);
        assertEquals(borrowing.getId(), dto.id());
        assertEquals(book.getTitle(), dto.bookTitle());
        assertEquals(book.getAuthor(), dto.bookAuthor());
        assertEquals(user.getName(), dto.userName());
        assertEquals(borrowing.getBorrowDate(), dto.borrowDate());
        assertEquals(borrowing.getDueDate(), dto.dueDate());
        assertEquals(borrowing.getReturnDate(), dto.returnDate());
    }

    @Test
    void toDto_shouldHandleNullReturnDate() {
        User user = User.builder().id(1L).name("Ali").build();
        Book book = Book.builder().id(2L).title("Kitap").author("Yazar").build();
        Borrowing borrowing = Borrowing.builder()
                .id(3L)
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Borrowing.Status.BORROWED)
                .build();
        BorrowingResponse dto = BorrowingMapper.toDto(borrowing);
        assertNull(dto.returnDate());
    }
} 