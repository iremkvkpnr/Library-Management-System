package com.iremkvkpnr.librarymanagement.model.mapper;

import com.iremkvkpnr.librarymanagement.model.dto.request.BorrowingRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.BorrowingResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowingMapper {

    public static Borrowing toEntity(BorrowingRequest request, User user, Book book) {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(14); // 2 hafta varsayılan

        return Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(today)
                .dueDate(dueDate)
                .status(Borrowing.Status.PENDING)
                .build();
    }

    public static BorrowingResponse toDto(Borrowing borrowing) {
        return new BorrowingResponse(
                borrowing.getId(),
                borrowing.getBook().getTitle(),
                borrowing.getBook().getAuthor(),
                borrowing.getUser().getName(),
                borrowing.getBorrowDate(),
                borrowing.getDueDate(),
                borrowing.getReturnDate()
        );
    }
}
