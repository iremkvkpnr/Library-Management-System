package com.iremkvkpnr.librarymanagement.controller;

import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.UserResponse;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.BookValidationException;
import com.iremkvkpnr.librarymanagement.model.exception.UserPrincipalNotFoundException;
import com.iremkvkpnr.librarymanagement.model.exception.UserValidationException;
import com.iremkvkpnr.librarymanagement.model.mapper.UserMapper;
import com.iremkvkpnr.librarymanagement.service.UserService;
import com.iremkvkpnr.librarymanagement.validation.UserValidation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserValidation userValidation;

    public UserController(UserService userService,UserValidation userValidation) {
        this.userService = userService;
        this.userValidation=userValidation;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        try {
            userValidation.validateUserInput(request);
            User user = userService.registerUser(UserMapper.toEntity(request));
            return new ResponseEntity<>(UserMapper.toDto(user), HttpStatus.CREATED);
        }
        catch (UserValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return 400 if validation fails
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserDetails(@PathVariable  Long id) {

        try {
            UserResponse response = userService.getUserDetails(id);
            return ResponseEntity.ok(response);
        } catch (UserPrincipalNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if user not found
        }
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('LIBRARIAN')") //
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserRequest request) {
        try {
            userValidation.validateUserInput(request);
            User updateUser=userService.updateUser(id, UserMapper.toEntity(request));
            return ResponseEntity.ok(UserMapper.toDto(updateUser));
        } catch (UserPrincipalNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build(); // Return 204 if successful deletion
        } catch (UserPrincipalNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if book not found
        }
    }
}
