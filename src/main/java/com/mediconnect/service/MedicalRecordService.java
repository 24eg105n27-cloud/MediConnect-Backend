package com.mediconnect.service;

import com.mediconnect.model.MedicalRecord;
import com.mediconnect.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public List<MedicalRecord> getAllRecords() {
        return medicalRecordRepository.findAllByOrderByIdDesc();
    }

    public List<MedicalRecord> getRecordsByPatient(String patientName) {
        return medicalRecordRepository.findByPatientNameIgnoreCase(patientName);
    }

    public List<MedicalRecord> getRecordsByPatientEmail(String patientEmail) {
        return medicalRecordRepository.findByPatientEmailIgnoreCaseOrderByIdDesc(patientEmail);
    }

    public Optional<MedicalRecord> getRecordById(Long id) {
        return medicalRecordRepository.findById(id);
    }

    public Optional<MedicalRecord> getRecordByIdAndPatientEmail(Long id, String patientEmail) {
        return medicalRecordRepository.findByIdAndPatientEmailIgnoreCase(id, patientEmail);
    }

    public MedicalRecord saveRecord(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    public void deleteRecord(Long id) {
        medicalRecordRepository.deleteById(id);
    }

    public long countTotal() {
        return medicalRecordRepository.count();
    }
}
