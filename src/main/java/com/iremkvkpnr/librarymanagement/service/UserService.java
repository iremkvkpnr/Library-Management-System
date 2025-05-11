package com.iremkvkpnr.librarymanagement.service;

import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.UserResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.UserPrincipalNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.UserValidationException;
import com.iremkvkpnr.librarymanagement.model.mapper.UserMapper;
import com.iremkvkpnr.librarymanagement.repository.BorrowingRepository;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for user operations.
 * Handles user registration, update, deletion, detail retrieval, and eligibility checks.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;

    public UserService(UserRepository userRepository, BorrowingRepository borrowingRepository) {
        this.userRepository = userRepository;
        this.borrowingRepository = borrowingRepository;
    }

    /**
     * Registers a new user.
     * @param request User registration request
     * @return Registered user's response DTO
     */
    @Transactional
    public UserResponse registerUser(UserRequest request) {
        User user = UserMapper.toEntity(request);
        User saved = userRepository.save(user);
        log.info("New user registered: {}", saved);
        return UserMapper.toDto(saved);
    }

    /**
     * Retrieves user details by ID.
     * @param id User ID
     * @return User response DTO
     * @throws UserPrincipalNotFoundException if user is not found
     */
    @Transactional
    public UserResponse getUserDetails(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found: id={}", id);
                    return new UserPrincipalNotFoundException("User not found with ID: " + id);
                });
        log.info("User details retrieved: {}", user);
        return UserMapper.toDto(user);
    }

    /**
     * Updates user information.
     * @param id User ID to update
     * @param request Updated user information
     * @return Updated user's response DTO
     * @throws UserPrincipalNotFoundException if user is not found
     */
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User to update not found: id={}", id);
                    return new UserPrincipalNotFoundException("User not found with ID: " + id);
                });

        if (request.name() != null && !request.name().trim().isEmpty()) {
            existingUser.setName(request.name());
        }
        if (request.email() != null && !request.email().trim().isEmpty()) {
            existingUser.setEmail(request.email());
        }
        if (request.password() != null && !request.password().trim().isEmpty()) {
            existingUser.setPassword(request.password());
        }
        if (request.phone() != null && !request.phone().trim().isEmpty()) {
            existingUser.setPhone(request.phone());
        }
        if (request.role() != null) {
            existingUser.setRole(request.role());
        }
        User updated = userRepository.save(existingUser);
        log.info("User updated: {}", updated);
        return UserMapper.toDto(updated);
    }

    /**
     * Deletes a user by ID.
     * @param id User ID to delete
     * @throws UserPrincipalNotFoundException if user is not found
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User to delete not found: id={}", id);
                    return new UserPrincipalNotFoundException("User not found with ID: " + id);
                });
        userRepository.delete(user);
        log.info("User deleted: {}", user);
    }

    /**
     * Checks if a user is eligible to borrow books (no overdue books).
     * @param userId User ID
     * @return true if eligible, otherwise throws exception
     * @throws UserValidationException if user has overdue books
     */
    public boolean isUserEligible(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserPrincipalNotFoundException("User not found with ID: " + userId));
        List<Borrowing> overdueBooks = borrowingRepository.findOverdueBooks(LocalDate.now());
        boolean hasOverdueBooks = overdueBooks.stream()
                .anyMatch(borrowing -> borrowing.getUser().getId().equals(userId));
        if (hasOverdueBooks) {
            throw new UserValidationException("User has overdue books and is not eligible to borrow.");
        }
        log.info("User eligibility check: userId={}, eligible={}", userId, !hasOverdueBooks);
        return true;
    }

}
