package com.mediconnect.repository;

import com.mediconnect.model.MedicalRecordShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordShareRepository extends JpaRepository<MedicalRecordShare, Long> {
    Optional<MedicalRecordShare> findByTokenHashAndRevokedFalseAndExpiresAtAfter(String tokenHash, Instant now);
    List<MedicalRecordShare> findByPatientEmailIgnoreCaseAndRevokedFalse(String patientEmail);
    Optional<MedicalRecordShare> findTopByPatientEmailIgnoreCaseAndRevokedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String patientEmail,
            Instant now
    );
}
