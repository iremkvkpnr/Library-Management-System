package com.iremkvkpnr.librarymanagement.model.mapper;

import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.dto.response.UserResponse;
import com.iremkvkpnr.librarymanagement.model.entity.User;

public class UserMapper {
    public static User toEntity(UserRequest userRequest) {
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
