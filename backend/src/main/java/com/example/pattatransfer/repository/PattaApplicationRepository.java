package com.example.pattatransfer.repository;

import com.example.pattatransfer.entity.PattaApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Patta Land Transfer applications database operations.
 */
@Repository
public interface PattaApplicationRepository extends JpaRepository<PattaApplication, Long> {

    /**
     * Retrieves all applications submitted by a specific citizen.
     * Spring Data JPA resolves User ID mapping automatically.
     * 
     * @param userId The ID of the User who applied
     * @return List of PattaApplication entities
     */
    List<PattaApplication> findByUserId(Long userId);
}
