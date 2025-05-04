package com.iremkvkpnr.librarymanagement.model.dto.response;

import java.time.LocalDate;

public record BorrowingResponse(
         Long id,
         String bookTitle,
         String bookAuthor,
         String userName,
         LocalDate borrowDate,
         LocalDate dueDate,
         LocalDate returnDate) {
}
