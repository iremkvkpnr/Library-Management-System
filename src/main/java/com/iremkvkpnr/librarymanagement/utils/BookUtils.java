package com.iremkvkpnr.librarymanagement.utils;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;

public class BookUtils {

    //used book validation
    public static Book.Genre parseGenre(String genre) {
        try {
            return Book.Genre.valueOf(genre.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BookValidationException("Invalid genre specified: " + genre);
        }
    }
}
