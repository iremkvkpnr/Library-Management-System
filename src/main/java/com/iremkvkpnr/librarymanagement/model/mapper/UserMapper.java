package com.iremkvkpnr.librarymanagement.model.mapper;

import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.UserResponse;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class UserMapper {
    public static User toEntity(UserRequest userRequest) {
        return User.builder()
                .name(userRequest.name())
                .email(userRequest.email())
                .password(userRequest.password())
                .phone(userRequest.phone())
                .role(userRequest.role())
                .userEligibility(userRequest.userEligibility())
                .build();
    }

    public static UserResponse toDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getUserEligibilityField(),
                user.getCreatedAt()
        );
    }
}
