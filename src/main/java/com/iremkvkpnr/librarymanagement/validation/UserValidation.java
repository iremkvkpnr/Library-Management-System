package com.iremkvkpnr.librarymanagement.validation;

import com.iremkvkpnr.librarymanagement.model.dto.request.UserRequest;
import com.iremkvkpnr.librarymanagement.model.exception.UserValidationException;
import org.springframework.stereotype.Component;

@Component
public class UserValidation {

    public void validateUserInput(UserRequest userRequest) {
        if (userRequest.name() == null || userRequest.name().trim().isEmpty()) {
            throw new UserValidationException(UserValidationException.EMPTY_NAME);
        }
        if (userRequest.email() == null || userRequest.email().trim().isEmpty()) {
            throw new UserValidationException(UserValidationException.EMPTY_EMAIL);
        }
        if (!isValidEmail(userRequest.email())) {
            throw new UserValidationException(UserValidationException.INVALID_EMAIL_FORMAT);
        }
        if (userRequest.phone() == null || userRequest.phone().trim().isEmpty()) {
            throw new UserValidationException(UserValidationException.EMPTY_PHONE);
        }
        // Diğer kontroller
    }

    private boolean isValidEmail(String email) {
        // Basit e-posta doğrulama
        return email.contains("@");
    }
}
