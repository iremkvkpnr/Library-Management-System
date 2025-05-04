package com.iremkvkpnr.librarymanagement.model.dto.request;

public record BookSearchRequest(
        String title,
        String author,
        String isbn,
        String genre
) {}

