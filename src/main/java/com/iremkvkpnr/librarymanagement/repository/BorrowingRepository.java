package com.iremkvkpnr.librarymanagement.repository;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findByUser(User user);
    List<Borrowing> findByBook(Book book);
    List<Borrowing> findByUserId(Long userId);
    // Gecikmiş kitapları bulma (dueDate geçmiş ve returnDate null olan kitaplar)
    @Query("SELECT b FROM Borrowing b WHERE b.dueDate < CURRENT_DATE AND b.returnDate IS NULL")
    List<Borrowing> findOverdueBooks(LocalDate currentDate);
}
