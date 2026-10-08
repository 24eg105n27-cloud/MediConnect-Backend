package com.mediconnect.controller;

import com.mediconnect.config.AuthenticatedUserInterceptor;
import com.mediconnect.dto.PatientReportedMedicalRecordRequest;
import com.mediconnect.model.MedicalRecord;
import com.mediconnect.model.User;
import com.mediconnect.service.MedicalRecordService;
import com.mediconnect.service.MedicalRecordShareService;
import com.mediconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class MedicalRecordControllerTest {

    @Test
    void patientHistoryIsSavedToAuthenticatedPatientAndMarkedUnverified() {
        RecordingMedicalRecordService records = new RecordingMedicalRecordService();
        MedicalRecordController controller = new MedicalRecordController(
                records,
                new MedicalRecordShareService(null, null),
                new UserService(null, null)
        );
        User patient = new User();
        patient.setName("Sample Patient");
        patient.setEmail("sample@example.com");
        patient.setRole("PATIENT");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(AuthenticatedUserInterceptor.USER_ATTRIBUTE, patient);
        var response = controller.createSelfReportedRecord(
                new PatientReportedMedicalRecordRequest(
                        "Example Clinic",
                        "Prior visit - patient supplied",
                        "Medication from paperwork",
                        "2026-05-12",
                        "Clinician listed on paperwork",
                        "Patient-entered history"
                ),
                request
        );

        assertEquals(201, response.getStatusCode().value());
        MedicalRecord saved = records.savedRecord;
        assertNotNull(saved);
        assertEquals("sample@example.com", saved.getPatientEmail());
        assertEquals("Sample Patient", saved.getPatientName());
        assertEquals("PATIENT_REPORTED", saved.getRecordSource());
        assertEquals("Example Clinic", saved.getHospital());
    }

    @Test
    void doctorCannotSubmitThroughPatientSelfReportEndpoint() {
        RecordingMedicalRecordService records = new RecordingMedicalRecordService();
        MedicalRecordController controller = new MedicalRecordController(
                records,
                new MedicalRecordShareService(null, null),
                new UserService(null, null)
        );
        User doctor = new User();
        doctor.setRole("DOCTOR");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(AuthenticatedUserInterceptor.USER_ATTRIBUTE, doctor);

        assertThrows(
                ResponseStatusException.class,
                () -> controller.createSelfReportedRecord(
                        new PatientReportedMedicalRecordRequest(
                                "Clinic", "History", null, "2026-05-12", null, null
                        ),
                        request
                )
        );
        assertNull(records.savedRecord);
    }

    private static final class RecordingMedicalRecordService extends MedicalRecordService {
        private MedicalRecord savedRecord;

        private RecordingMedicalRecordService() {
            super(null);
        }

        @Override
        public MedicalRecord saveRecord(MedicalRecord record) {
            savedRecord = record;
            return record;
        }
    }
}
