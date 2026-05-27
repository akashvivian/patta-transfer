package com.example.pattatransfer.service;

import com.example.pattatransfer.entity.PattaApplication;
import com.example.pattatransfer.entity.User;
import com.example.pattatransfer.repository.PattaApplicationRepository;
import com.example.pattatransfer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing land transfer applications lifecycle.
 */
@Service
public class PattaApplicationServiceImpl implements PattaApplicationService {

    private final PattaApplicationRepository pattaApplicationRepository;
    private final UserRepository userRepository;

    @Autowired
    public PattaApplicationServiceImpl(PattaApplicationRepository pattaApplicationRepository, UserRepository userRepository) {
        this.pattaApplicationRepository = pattaApplicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PattaApplication applyPatta(PattaApplication application, Long userId) {
        // 1. Fetch user to ensure the citizen exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId + "! Unable to file application."));

        // 2. Set user association and initial status
        application.setUser(user);
        application.setStatus("Pending");

        // 3. Save the application to database
        return pattaApplicationRepository.save(application);
    }

    @Override
    public List<PattaApplication> getApplicationsByUserId(Long userId) {
        // Retrieve and return citizen-specific application forms
        return pattaApplicationRepository.findByUserId(userId);
    }

    @Override
    public List<PattaApplication> getAllApplications() {
        // Retrieve and return all applications in the system
        return pattaApplicationRepository.findAll();
    }

    @Override
    public PattaApplication getApplicationById(Long id) {
        // Fetch details of a single application
        return pattaApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application form not found with ID: " + id));
    }

    @Override
    public PattaApplication updateStatus(Long id, String status) {
        // 1. Retrieve the application
        PattaApplication application = getApplicationById(id);

        // 2. Validate the new status
        if (!status.equalsIgnoreCase("Approved") && !status.equalsIgnoreCase("Rejected") && !status.equalsIgnoreCase("Pending")) {
            throw new RuntimeException("Invalid status value: " + status + ". Allowed values are 'Approved', 'Rejected', or 'Pending'.");
        }

        // 3. Update and save status
        application.setStatus(status);
        return pattaApplicationRepository.save(application);
    }
}
