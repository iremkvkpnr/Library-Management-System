package com.iremkvkpnr.librarymanagement.model.dto.response;

import com.iremkvkpnr.librarymanagement.model.entity.Book;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        LocalDate publicationDate,
        Book.Genre genre,
        int availableCopies,
        int totalCopies,
        LocalDateTime createdAt
) { }
