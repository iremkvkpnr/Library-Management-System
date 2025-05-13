package com.iremkvkpnr.librarymanagement.repository;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findByUserId(Long userId);

    @Query("SELECT b FROM Borrowing b WHERE b.dueDate < CURRENT_DATE AND b.returnDate IS NULL")
    List<Borrowing> findOverdueBooks(LocalDate currentDate);

    @Query("SELECT b FROM Borrowing b WHERE b.user.id = :userId AND b.book.id = :bookId AND b.status = 'BORROWED'")
    Optional<Borrowing> findActiveBorrowingByUserAndBook(Long userId, Long bookId);

    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.user.id = :userId AND b.status = 'BORROWED'")
    long countActiveBorrowingsByUser(Long userId);

    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.user.id = :userId AND b.dueDate < CURRENT_DATE AND b.returnDate IS NULL")
    long countOverdueBooksByUser(Long userId);
}
