package com.iremkvkpnr.librarymanagement.model.mapper;

import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.UserResponse;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.model.exception.UserValidationException;

public class UserMapper {
    public static User toEntity(UserRequest userRequest) {
        if(userRequest == null) {
            throw new UserValidationException("User request cannot be null");
        }
        if(userRequest.role() == null) {
            throw new UserValidationException("User role must be specified");
        }
        if(userRequest.email() == null || !userRequest.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new UserValidationException("Invalid email format");
        }
        return User.builder()
                .name(userRequest.name())
                .email(userRequest.email())
                .password(userRequest.password())
                .phone(userRequest.phone())
                .role(userRequest.role())
                .build();
    }

    public static UserResponse toDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
