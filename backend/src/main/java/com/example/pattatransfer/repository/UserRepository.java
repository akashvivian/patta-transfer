package com.example.pattatransfer.repository;

import com.example.pattatransfer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User persistence operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their registered email address.
     * Useful for checking duplicate emails during signup and authenticating credentials during login.
     * 
     * @param email user's email
     * @return Optional containing the User if found
     */
    Optional<User> findByEmail(String email);
}
