package com.mediconnect.controller;

import com.mediconnect.dto.ApiResponse;
import com.mediconnect.dto.MedicalRecordShareResponse;
import com.mediconnect.dto.MedicalRecordShareStatusResponse;
import com.mediconnect.dto.PatientReportedMedicalRecordRequest;
import com.mediconnect.dto.ResolveMedicalRecordShareRequest;
import com.mediconnect.dto.SharedMedicalRecordsResponse;
import com.mediconnect.config.AuthenticatedUserInterceptor;
import com.mediconnect.model.MedicalRecord;
import com.mediconnect.model.User;
import com.mediconnect.service.MedicalRecordService;
import com.mediconnect.service.MedicalRecordShareService;
import com.mediconnect.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final MedicalRecordShareService shareService;
    private final UserService userService;

    public MedicalRecordController(
            MedicalRecordService medicalRecordService,
            MedicalRecordShareService shareService,
            UserService userService
    ) {
        this.medicalRecordService = medicalRecordService;
        this.shareService = shareService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllRecords(
            HttpServletRequest request,
            @RequestParam(required = false) String patientName) {
        User user = currentUser(request);
        if (isDoctor(user)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Doctors must use a patient-approved medical-record share"
            );
        }
        if (patientName != null && !patientName.isBlank()
                && !patientName.equalsIgnoreCase(user.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patients can only view their own records");
        }
        return ResponseEntity.ok(medicalRecordService.getRecordsByPatientEmail(user.getEmail()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getRecordById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        User user = currentUser(request);
        if (isDoctor(user)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Doctors must use a patient-approved medical-record share"
            );
        }
        var record = medicalRecordService.getRecordByIdAndPatientEmail(id, user.getEmail());
        return record
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MedicalRecord> createRecord(
            @RequestBody MedicalRecord record,
            HttpServletRequest request
    ) {
        User user = currentUser(request);
        if (!isDoctor(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only doctors can create medical records");
        }
        if (record.getPatientEmail() == null || record.getPatientEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient email is required");
        }
        User patient = userService.getProfileByEmail(record.getPatientEmail())
                .filter(account -> "PATIENT".equalsIgnoreCase(account.getRole()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient account not found"));
        record.setPatientEmail(patient.getEmail());
        record.setPatientName(patient.getName());
        record.setDoctor(user.getName());
        record.setRecordSource("CLINICIAN_ENTERED");
        MedicalRecord saved = medicalRecordService.saveRecord(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/self-reported")
    public ResponseEntity<MedicalRecord> createSelfReportedRecord(
            @Valid @RequestBody PatientReportedMedicalRecordRequest requestBody,
            HttpServletRequest request
    ) {
        User patient = currentUser(request);
        if (!"PATIENT".equalsIgnoreCase(patient.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patients can add personal history");
        }
        MedicalRecord record = new MedicalRecord();
        record.setPatientEmail(patient.getEmail());
        record.setPatientName(patient.getName());
        record.setHospital(requestBody.hospital().trim());
        record.setDisease(requestBody.disease().trim());
        record.setMedicines(requestBody.medicines());
        record.setVisitDate(requestBody.visitDate().trim());
        record.setDoctor(requestBody.doctor());
        record.setNotes(requestBody.notes());
        record.setRecordSource("PATIENT_REPORTED");
        MedicalRecord saved = medicalRecordService.saveRecord(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRecord(@PathVariable Long id, HttpServletRequest request) {
        User doctor = currentUser(request);
        if (!isDoctor(doctor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only doctors can delete medical records");
        }
        boolean createdByDoctor = medicalRecordService.getRecordById(id)
                .filter(existing -> existing.getDoctor() != null
                        && existing.getDoctor().equalsIgnoreCase(doctor.getName()))
                .isPresent();
        if (!createdByDoctor) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Medical record not found or not created by this doctor"
            );
        }
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.ok(new ApiResponse(true, "Medical record deleted successfully"));
    }

    @PostMapping("/shares")
    public ResponseEntity<MedicalRecordShareResponse> createShare(HttpServletRequest request) {
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.noStore())
                .header("Referrer-Policy", "no-referrer")
                .body(shareService.createShare(currentUser(request)));
    }

    @GetMapping("/shares")
    public ResponseEntity<MedicalRecordShareStatusResponse> getShareStatus(HttpServletRequest request) {
        User user = currentUser(request);
        if (!"PATIENT".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patients can manage records sharing");
        }
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.noStore())
                .body(shareService.getShareStatus(user.getEmail()));
    }

    @DeleteMapping("/shares")
    public ResponseEntity<ApiResponse> revokeShare(HttpServletRequest request) {
        User user = currentUser(request);
        if (!"PATIENT".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patients can manage records sharing");
        }
        shareService.revokeShares(user.getEmail());
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.noStore())
                .body(new ApiResponse(true, "Medical-record access has been revoked"));
    }

    @PostMapping("/shared")
    public ResponseEntity<SharedMedicalRecordsResponse> getSharedRecords(
            @Valid @RequestBody ResolveMedicalRecordShareRequest shareRequest,
            HttpServletRequest request
    ) {
        if (!isDoctor(currentUser(request))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A doctor account is required to view shared records");
        }
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.noStore())
                .header("Referrer-Policy", "no-referrer")
                .header("X-Robots-Tag", "noindex, nofollow, noarchive")
                .body(shareService.getSharedRecords(shareRequest.token()));
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute(AuthenticatedUserInterceptor.USER_ATTRIBUTE);
    }

    private boolean isDoctor(User user) {
        return "DOCTOR".equalsIgnoreCase(user.getRole());
    }
}
