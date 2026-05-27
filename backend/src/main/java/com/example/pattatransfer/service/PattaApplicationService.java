package com.example.pattatransfer.service;

import com.example.pattatransfer.entity.PattaApplication;

import java.util.List;

/**
 * Service interface defining land title transfer application operations.
 */
public interface PattaApplicationService {

    /**
     * Files a new Patta transfer application associated with a citizen.
     * 
     * @param application The details of the application
     * @param userId The ID of the submitting citizen
     * @return The created application entity
     */
    PattaApplication applyPatta(PattaApplication application, Long userId);

    /**
     * Fetches all applications filed by a specific citizen.
     * 
     * @param userId Citizen's ID
     * @return List of applications
     */
    List<PattaApplication> getApplicationsByUserId(Long userId);

    /**
     * Retrieves all land transfer applications filed in the system (Admin operation).
     * 
     * @return List of all applications
     */
    List<PattaApplication> getAllApplications();

    /**
     * Fetches details of a specific application.
     * 
     * @param id The application ID
     * @return The application details
     */
    PattaApplication getApplicationById(Long id);

    /**
     * Updates the processing status of an application (Admin operation).
     * 
     * @param id The application ID
     * @param status The new status ('Approved', 'Rejected')
     * @return The updated application entity
     */
    PattaApplication updateStatus(Long id, String status);
}
