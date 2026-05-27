package com.example.pattatransfer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entity representing a Patta Land Transfer application submitted by a citizen.
 */
@Entity
@Table(name = "patta_applications")
public class PattaApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One mapping to link applications to the user who applied
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Applicant Name is required")
    @Size(min = 2, max = 100, message = "Applicant Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s.]+$", message = "Applicant Name must contain letters, spaces, and dots only")
    @Column(name = "applicant_name", nullable = false)
    private String applicantName;

    @NotBlank(message = "Survey Number is required")
    @Pattern(regexp = "^\\d+(/[a-zA-Z0-9]+)?$", message = "Survey Number must be numeric or in standard sub-division format (e.g. 275 or 275/3A)")
    @Column(name = "survey_number", nullable = false)
    private String surveyNumber;

    @NotBlank(message = "Patta Number is required")
    @Pattern(regexp = "^\\d+$", message = "Patta Number must be numeric only (e.g. 202)")
    @Column(name = "patta_number", nullable = false)
    private String pattaNumber;

    @NotBlank(message = "Transfer Type is required")
    @Column(name = "transfer_type", nullable = false)
    private String transferType; // ISD or NISD

    @NotBlank(message = "Document Type is required")
    @Column(name = "document_type", nullable = false)
    private String documentType; // Sale Deed, Settlement Deed, etc.

    @NotBlank(message = "District is required")
    @Column(name = "district", nullable = false)
    private String district;

    @NotBlank(message = "Taluk is required")
    @Column(name = "taluk", nullable = false)
    private String taluk;

    @Column(name = "property_document", nullable = false)
    private String propertyDocument; // File path/name stored on local storage

    @Column(name = "identity_proof", nullable = false)
    private String identityProof; // File path/name stored on local storage

    @Column(name = "address_proof", nullable = false)
    private String addressProof; // File path/name stored on local storage

    @Column(nullable = false)
    private String status = "Pending"; // 'Pending', 'Approved', 'Rejected'

    // Default Constructor (Required by JPA)
    public PattaApplication() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getSurveyNumber() {
        return surveyNumber;
    }

    public void setSurveyNumber(String surveyNumber) {
        this.surveyNumber = surveyNumber;
    }

    public String getPattaNumber() {
        return pattaNumber;
    }

    public void setPattaNumber(String pattaNumber) {
        this.pattaNumber = pattaNumber;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTaluk() {
        return taluk;
    }

    public void setTaluk(String taluk) {
        this.taluk = taluk;
    }

    public String getPropertyDocument() {
        return propertyDocument;
    }

    public void setPropertyDocument(String propertyDocument) {
        this.propertyDocument = propertyDocument;
    }

    public String getIdentityProof() {
        return identityProof;
    }

    public void setIdentityProof(String identityProof) {
        this.identityProof = identityProof;
    }

    public String getAddressProof() {
        return addressProof;
    }

    public void setAddressProof(String addressProof) {
        this.addressProof = addressProof;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
