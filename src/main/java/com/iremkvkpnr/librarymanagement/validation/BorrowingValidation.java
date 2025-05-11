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

    public void validateBorrowing(Long userId, Long bookId) {
        if (userId == null) {
            throw new BorrowingValidationException(BorrowingValidationException.EMPTY_USER_ID);
        }
        if (bookId == null) {
            throw new BorrowingValidationException(BorrowingValidationException.EMPTY_BOOK_ID);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BorrowingValidationException("User not found"));

        if (user.getRole() == User.Role.LIBRARIAN) {
            throw new BorrowingValidationException("Librarians cannot borrow books");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BorrowingValidationException("Book not found"));
        if (book.getAvailableCopies() <= 0) {
            throw new BorrowingValidationException(BorrowingValidationException.BOOK_NOT_AVAILABLE);
        }
    }


}
