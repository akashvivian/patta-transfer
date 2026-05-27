package com.example.pattatransfer.controller;

import com.example.pattatransfer.entity.User;
import com.example.pattatransfer.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for managing Citizen profiles (Registration and Log In endpoints).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles User Registration requests.
     * Validates input fields and saves new user details.
     * 
     * Sample API Request: POST http://localhost:8080/api/users/register
     * Sample Request Body:
     * {
     *     "name": "John Doe",
     *     "mobile": "9876543210",
     *     "address": "123 Main Street, Chennai",
     *     "email": "john@example.com",
     *     "password": "password123"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        // 1. Check for field validation errors (e.g. invalid email format, short password)
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            response.put("success", false);
            response.put("message", "Validation failed: " + errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // 2. Register the user using service
            User registeredUser = userService.registerUser(user);
            
            // 3. Clear sensitive details like password before sending response
            registeredUser.setPassword(null);

            response.put("success", true);
            response.put("message", "Registration successful!");
            response.put("data", registeredUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException ex) {
            // Handle exceptions like "Email already exists"
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Handles User/Admin Login requests.
     * Validates credentials and returns authenticated user profile with role type.
     * 
     * Sample API Request: POST http://localhost:8080/api/users/login
     * Sample Request Body:
     * {
     *     "email": "john@example.com",
     *     "password": "password123"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        // 1. Check for field validation errors (e.g. invalid email format)
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            response.put("success", false);
            response.put("message", "Validation failed: " + errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // 2. Verify credentials
            User authenticatedUser = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            
            // 3. Clear password before sending response
            authenticatedUser.setPassword(null);

            response.put("success", true);
            response.put("message", "Login successful!");
            response.put("data", authenticatedUser);
            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            // Handle exceptions like "User not found" or "Invalid password"
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * DTO (Data Transfer Object) class to map incoming Login request payload.
     */
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
