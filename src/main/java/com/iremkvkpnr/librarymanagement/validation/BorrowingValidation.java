package com.iremkvkpnr.librarymanagement.validation;

import com.iremkvkpnr.librarymanagement.model.dto.request.BorrowingRequest;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.BorrowingValidationException;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BorrowingValidation {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BorrowingValidation(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public void validateBorrowing(BorrowingRequest borrowingRequest) {
        if (borrowingRequest.userId() == null) {
            throw new BorrowingValidationException(BorrowingValidationException.EMPTY_USER_ID);
        }

        if (borrowingRequest.bookId() == null) {
            throw new BorrowingValidationException(BorrowingValidationException.EMPTY_BOOK_ID);
        }

        User user = userRepository.findById(borrowingRequest.userId())
                .orElseThrow(() -> new BorrowingValidationException("User not found"));

        if (user.getRole() == User.Role.LIBRARIAN) {
            throw new BorrowingValidationException("Librarians cannot borrow books");
        }

        if (!user.isUserEligibility()) {
            throw new BorrowingValidationException(BorrowingValidationException.USER_NOT_ELIGIBLE);
        }

        Book book = bookRepository.findById(borrowingRequest.bookId())
                .orElseThrow(() -> new BorrowingValidationException("Book not found"));

        // Kitapta yeterli kopya olmalı
        if (book.getAvailableCopies() <= 0) {
            throw new BorrowingValidationException(BorrowingValidationException.BOOK_NOT_AVAILABLE);
        }

        // Kitapta zaten ödünç alınmış bir kopya varsa ve başka kopyalar mevcutsa,
        // ödünç verme işlemi yapılabilir.
        long borrowedBooksCount = book.getBorrowings().stream()
                .filter(borrowing -> borrowing.getStatus() == Borrowing.Status.BORROWED)
                .count();

        if (borrowedBooksCount >= book.getTotalCopies()) {
            throw new BorrowingValidationException(BorrowingValidationException.BOOK_ALREADY_BORROWED);
        }
    }
}
