package com.mediconnect.repository;

import com.mediconnect.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientNameIgnoreCase(String patientName);
    List<MedicalRecord> findByPatientEmailIgnoreCaseOrderByIdDesc(String patientEmail);
    Optional<MedicalRecord> findByIdAndPatientEmailIgnoreCase(Long id, String patientEmail);
    List<MedicalRecord> findAllByOrderByIdDesc();
}
