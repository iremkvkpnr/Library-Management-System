package com.iremkvkpnr.librarymanagement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record BookRequest(

        @NotBlank(message = "Title must not be blank")
        String title,
        @NotBlank(message = "Author must not be blank")
        String author,
        @NotBlank(message = "ISBN must not be blank")
        String isbn,
        @NotBlank(message = "Genre must not be blank")
        String genre,
        @Positive(message = "Total copies must be greater than zero")
        int totalCopies,
        @NotNull(message = "Publication date must not be null")
        LocalDate publicationDate
) {}
