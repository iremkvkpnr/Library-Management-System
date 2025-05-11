package com.iremkvkpnr.librarymanagement.repository;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    // Method to check if a book exists by its ISBN
    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE " +
            "(:title IS NULL OR b.title LIKE %:title%) AND " +
            "(:author IS NULL OR b.author LIKE %:author%) AND " +
            "(:isbn IS NULL OR b.isbn LIKE %:isbn%) AND " +
            "(:genre IS NULL OR b.genre LIKE %:genre%)")
    Page<Book> searchBooks(@Param("title") String title,
                           @Param("author") String author,
                           @Param("isbn") String isbn,
                           @Param("genre") String genre,
                           Pageable pageable);

}
