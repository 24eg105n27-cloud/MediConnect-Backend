package com.mediconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_record", indexes = {
        @Index(name = "idx_medical_record_patient_email", columnList = "patient_email")
})
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "patient_email")
    private String patientEmail;

    @Column(name = "record_source", length = 32)
    private String recordSource;

    @Column(name = "hospital")
    private String hospital;

    @Column(name = "doctor")
    private String doctor;

    @Column(name = "disease")
    private String disease;

    @Column(name = "medicines")
    private String medicines;

    @Column(name = "visit_date")
    private String visitDate;

    @Column(name = "notes", length = 1000)
    private String notes;

    public MedicalRecord() {
    }

    public MedicalRecord(String patientName, String hospital, String doctor, String disease, String medicines, String visitDate, String notes) {
        this.patientName = patientName;
        this.hospital = hospital;
        this.doctor = doctor;
        this.disease = disease;
        this.medicines = medicines;
        this.visitDate = visitDate;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(String recordSource) {
        this.recordSource = recordSource;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
