package com.example.pattatransfer.service;

import com.example.pattatransfer.entity.User;

/**
 * Service interface defining citizen profile operations (Registration and Authentication).
 */
public interface UserService {

    /**
     * Registers a new citizen user.
     * 
     * @param user The user details to save
     * @return The registered User object
     */
    User registerUser(User user);

    /**
     * Authenticates a user based on their email and password.
     * 
     * @param email The registered email
     * @param password The login password
     * @return The authenticated User object
     */
    User loginUser(String email, String password);
}
