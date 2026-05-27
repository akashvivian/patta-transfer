package com.example.pattatransfer.controller;

import com.example.pattatransfer.entity.PattaApplication;
import com.example.pattatransfer.service.PattaApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * REST Controller for managing Patta Land Transfer applications and files.
 */
@RestController
@RequestMapping("/api/patta")
public class PattaApplicationController {

    private final PattaApplicationService pattaApplicationService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    public PattaApplicationController(PattaApplicationService pattaApplicationService) {
        this.pattaApplicationService = pattaApplicationService;
    }

    /**
     * Files a new Patta transfer application with 3 document file attachments.
     * Uses multipart form-data.
     * 
     * Upload Rules:
     * - File format must be PDF, JPG, or PNG
     * - Maximum file size is 3MB (checked via spring.servlet.multipart.max-file-size automatically and manually)
     */
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> applyPatta(
            @RequestParam("userId") Long userId,
            @RequestParam("applicantName") String applicantName,
            @RequestParam("surveyNumber") String surveyNumber,
            @RequestParam("pattaNumber") String pattaNumber,
            @RequestParam("transferType") String transferType,
            @RequestParam("documentType") String documentType,
            @RequestParam("district") String district,
            @RequestParam("taluk") String taluk,
            @RequestParam("propertyDocument") MultipartFile propertyDoc,
            @RequestParam("identityProof") MultipartFile identityProofDoc,
            @RequestParam("addressProof") MultipartFile addressProofDoc
    ) {
        Map<String, Object> response = new HashMap<>();

        // 1. Basic validation of required text fields
        if (applicantName == null || applicantName.trim().isEmpty() ||
            surveyNumber == null || surveyNumber.trim().isEmpty() ||
            pattaNumber == null || pattaNumber.trim().isEmpty() ||
            transferType == null || transferType.trim().isEmpty() ||
            documentType == null || documentType.trim().isEmpty() ||
            district == null || district.trim().isEmpty() ||
            taluk == null || taluk.trim().isEmpty()) {
            
            response.put("success", false);
            response.put("message", "All text fields are required!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 1b. Format pattern validations
        if (!applicantName.matches("^[a-zA-Z\\s.]+$")) {
            response.put("success", false);
            response.put("message", "Validation failed: Applicant Name must contain letters, spaces, and dots only!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!surveyNumber.matches("^\\d+(/[a-zA-Z0-9]+)?$")) {
            response.put("success", false);
            response.put("message", "Validation failed: Survey Number must be numeric or in standard sub-division format (e.g. 275 or 275/3A)!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!pattaNumber.matches("^\\d+$")) {
            response.put("success", false);
            response.put("message", "Validation failed: Patta Number must contain numbers only (e.g. 202)!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 2. Validate file existence
        if (propertyDoc.isEmpty() || identityProofDoc.isEmpty() || addressProofDoc.isEmpty()) {
            response.put("success", false);
            response.put("message", "All three documents (Property Document, Identity Proof, Address Proof) are required!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 3. Validate file types & sizes (Max 3MB each)
        try {
            validateFile(propertyDoc, "Property Document");
            validateFile(identityProofDoc, "Identity Proof");
            validateFile(addressProofDoc, "Address Proof");
        } catch (IllegalArgumentException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // 4. Save files to local storage and generate unique names to prevent collision
            String propDocName = saveUploadedFile(propertyDoc);
            String idProofName = saveUploadedFile(identityProofDoc);
            String addrProofName = saveUploadedFile(addressProofDoc);

            // 5. Populate Application Entity
            PattaApplication application = new PattaApplication();
            application.setApplicantName(applicantName);
            application.setSurveyNumber(surveyNumber);
            application.setPattaNumber(pattaNumber);
            application.setTransferType(transferType);
            application.setDocumentType(documentType);
            application.setDistrict(district);
            application.setTaluk(taluk);
            application.setPropertyDocument(propDocName);
            application.setIdentityProof(idProofName);
            application.setAddressProof(addrProofName);

            // 6. Save through Service
            PattaApplication savedApp = pattaApplicationService.applyPatta(application, userId);

            response.put("success", true);
            response.put("message", "Application submitted successfully!");
            response.put("data", savedApp);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "An error occurred while filing the application: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Fetches all applications filed by a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getApplicationsByUser(@PathVariable("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PattaApplication> list = pattaApplicationService.getApplicationsByUserId(userId);
            response.put("success", true);
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Fetches all applications submitted in the system (Admin operation).
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllApplications() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PattaApplication> list = pattaApplicationService.getAllApplications();
            response.put("success", true);
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Updates status of an application (Approve / Reject) (Admin operation).
     * Accepts query parameter `status`.
     * E.g. PUT http://localhost:8080/api/patta/1/status?status=Approved
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            PattaApplication updatedApp = pattaApplicationService.updateStatus(id, status);
            response.put("success", true);
            response.put("message", "Application status updated to: " + status);
            response.put("data", updatedApp);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Internal error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Serves uploaded files securely by filename.
     * Admin or Citizen can click and view files.
     * 
     * GET http://localhost:8080/api/patta/files/{filename}
     */
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Determine content type dynamically
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Helper method to validate files locally using byte signatures (magic numbers).
     */
    private void validateFile(MultipartFile file, String label) {
        // Size validation (3MB in bytes = 3,145,728)
        if (file.getSize() > 3 * 1024 * 1024) {
            throw new IllegalArgumentException(label + " exceeds maximum size limit of 3MB!");
        }

        // Secure magic number byte signature check
        try (java.io.InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            int bytesRead = is.read(header);

            if (bytesRead < 4) {
                throw new IllegalArgumentException(label + " is empty or invalid!");
            }

            // Convert first 4 bytes to hexadecimal representation
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                hex.append(String.format("%02X", header[i]));
            }
            String signature = hex.toString();

            // Match magic numbers:
            // PDF: %PDF (25 50 44 46)
            // PNG: 89 50 4E 47
            // JPEG/JPG: FF D8 FF
            boolean isPDF = signature.startsWith("25504446");
            boolean isPNG = signature.startsWith("89504E47");
            boolean isJPEG = signature.startsWith("FFD8FF");

            if (!isPDF && !isPNG && !isJPEG) {
                throw new IllegalArgumentException(label + " format is not supported! Please upload genuine PDF, JPG, or PNG files only.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read " + label + " content for verification.");
        }
    }

    /**
     * Helper method to save file locally on disk.
     */
    private String saveUploadedFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "document";
        }
        
        // Prevent path traversal
        String cleanedFilename = Paths.get(originalFilename).getFileName().toString();
        // Append unique timestamp prefix
        String uniqueFilename = System.currentTimeMillis() + "_" + cleanedFilename;

        Path targetPath = Paths.get(uploadDir).resolve(uniqueFilename).normalize();
        
        // Copy file input stream to targeted local path on system
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    /**
     * Global handler for file upload size exceptions (thrown by Spring Boot).
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "One or more files exceed the maximum allowed size limit of 3MB!");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }
}
