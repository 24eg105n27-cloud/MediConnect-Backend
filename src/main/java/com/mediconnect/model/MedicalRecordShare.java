package com.mediconnect.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "medical_record_shares", indexes = {
        @Index(name = "idx_medical_record_share_patient_email", columnList = "patient_email")
})
public class MedicalRecordShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "patient_email", nullable = false)
    private String patientEmail;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    public MedicalRecordShare() {
    }

    public MedicalRecordShare(String tokenHash, String patientEmail, Instant createdAt, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.patientEmail = patientEmail;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    public Long getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
