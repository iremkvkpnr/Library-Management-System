package com.iremkvkpnr.librarymanagement.service;

import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.UserResponse;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.BookNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.UserPrincipalNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.UserValidationException;
import com.iremkvkpnr.librarymanagement.model.mapper.BookMapper;
import com.iremkvkpnr.librarymanagement.model.mapper.UserMapper;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public UserResponse getUserDetails(Long id) {
        User user=userRepository.findById(id)
                .orElseThrow(() -> new UserPrincipalNotFoundException("User not found with ID: " + id));

        return UserMapper.toDto(user);
    }

    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserPrincipalNotFoundException("User not found with ID: " + id));

        // Updating the user details with the provided information
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null && ! user.getEmail().trim().isEmpty()) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        existingUser.setUserEligibility(user.getUserEligibilityField());
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserPrincipalNotFoundException("Book not found with ID: " + id));
        userRepository.delete(user);
    }

}
