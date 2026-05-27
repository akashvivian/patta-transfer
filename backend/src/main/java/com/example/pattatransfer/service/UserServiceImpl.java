package com.example.pattatransfer.service;

import com.example.pattatransfer.entity.User;
import com.example.pattatransfer.repository.UserRepository;
import com.example.pattatransfer.util.HashUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for managing citizens registration and login verification.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        // 1. Check if user already exists with the given email
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email is already registered! Please use a different email or log in.");
        }

        // 2. Set default role to citizen (USER) if not defined
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }

        // 3. Hash the password before saving for security
        user.setPassword(HashUtility.hashSHA256(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String email, String password) {
        // 1. Retrieve the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with the provided email!"));

        // 2. Verify password using SHA-256 hash comparison
        String hashedInput = HashUtility.hashSHA256(password);
        if (!user.getPassword().equals(hashedInput)) {
            throw new RuntimeException("Invalid password! Please try again.");
        }

        return user;
    }
}
