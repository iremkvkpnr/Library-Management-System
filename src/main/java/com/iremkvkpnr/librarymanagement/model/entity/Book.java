package com.iremkvkpnr.librarymanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = "isbn")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    private LocalDate publicationDate;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private int availableCopies=0;

    private int totalCopies=0;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<Borrowing> borrowings;

    public enum Genre {
        FICTION,
        NON_FICTION,
        SCIENCE,
        TECHNOLOGY,
        HISTORY,
        FANTASY,
        BIOGRAPHY,
        OTHER;

        public static Genre fromString(String genre) {
            try {
                return Genre.valueOf(genre.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new com.iremkvkpnr.librarymanagement.model.exception.BookValidationException("Invalid genre specified: " + genre);
            }
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
