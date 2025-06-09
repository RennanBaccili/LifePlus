package org.dasher.speed.taskmanagement.validation;

import org.dasher.speed.taskmanagement.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class UserValidator {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    private final UserService userService;

    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    public ValidationResult validateRegistration(String email, String password, String confirmPassword) {
        List<String> errors = new ArrayList<>();

        validateEmail(email, errors);
        validatePassword(password, errors);
        validatePasswordMatch(password, confirmPassword, errors);

        return new ValidationResult(errors);
    }

    private void validateEmail(String email, List<String> errors) {
        if (email == null || email.trim().isEmpty()) {
            errors.add("Email is required");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.add("Invalid email format");
            return;
        }

        if (userService.emailExists(email)) {
            errors.add("Email already registered");
        }
    }

    private void validatePassword(String password, List<String> errors) {
        if (password == null || password.trim().isEmpty()) {
            errors.add("Password is required");
            return;
        }

        if (!pattern.matcher(password).matches()) {
            errors.add("Password must contain at least 8 characters, including uppercase, lowercase, numbers and special characters");
        }
    }

    private void validatePasswordMatch(String password, String confirmPassword, List<String> errors) {
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            errors.add("Passwords do not match");
        }
    }

    public static class ValidationResult {
        private final List<String> errors;

        public ValidationResult(List<String> errors) {
            this.errors = errors;
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getFirstError() {
            return errors.isEmpty() ? null : errors.get(0);
        }
    }
} 