package com.mediconnect.service;

import com.mediconnect.model.MedicalRecord;
import com.mediconnect.model.MedicalRecordShare;
import com.mediconnect.model.User;
import com.mediconnect.repository.MedicalRecordRepository;
import com.mediconnect.repository.MedicalRecordShareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordShareServiceTest {

    private static final String PATIENT_EMAIL = "patient@example.com";

    @Mock
    private MedicalRecordShareRepository shareRepository;

    @Mock
    private MedicalRecordRepository recordRepository;

    private MedicalRecordShareService service;

    @BeforeEach
    void setUp() {
        service = new MedicalRecordShareService(shareRepository, recordRepository);
    }

    @Test
    void createShareReturnsShortLivedTokenAndStoresOnlyItsHash() {
        User patient = patient();
        when(recordRepository.findByPatientEmailIgnoreCaseOrderByIdDesc(PATIENT_EMAIL))
                .thenReturn(List.of(record()));
        when(shareRepository.findByPatientEmailIgnoreCaseAndRevokedFalse(PATIENT_EMAIL))
                .thenReturn(List.of());
        when(shareRepository.save(any(MedicalRecordShare.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Instant beforeCreate = Instant.now();
        var response = service.createShare(patient);
        Instant afterCreate = Instant.now();

        ArgumentCaptor<MedicalRecordShare> savedShare = ArgumentCaptor.forClass(MedicalRecordShare.class);
        verify(shareRepository).save(savedShare.capture());
        assertNotNull(response.token());
        assertEquals(43, response.token().length());
        assertEquals(1, response.recordCount());
        assertNotEquals(response.token(), savedShare.getValue().getTokenHash());
        assertEquals(64, savedShare.getValue().getTokenHash().length());
        assertTrue(savedShare.getValue().getTokenHash().matches("[0-9a-f]{64}"));
        assertTrue(response.expiresAt().isAfter(beforeCreate.plus(Duration.ofMinutes(14))));
        assertTrue(response.expiresAt().isBefore(afterCreate.plus(Duration.ofMinutes(16))));
    }

    @Test
    void createShareCanBeCreatedBeforeRecordsAreLinked() {
        when(recordRepository.findByPatientEmailIgnoreCaseOrderByIdDesc(PATIENT_EMAIL))
                .thenReturn(List.of());
        when(shareRepository.findByPatientEmailIgnoreCaseAndRevokedFalse(PATIENT_EMAIL))
                .thenReturn(List.of());
        when(shareRepository.save(any(MedicalRecordShare.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.createShare(patient());

        assertNotNull(response.token());
        assertEquals(0, response.recordCount());
        verify(shareRepository).save(any(MedicalRecordShare.class));
    }

    @Test
    void resolveShareReturnsOnlyApprovedRecordFields() throws Exception {
        String rawToken = "doctor-approved-token";
        Instant expiry = Instant.now().plus(Duration.ofMinutes(5));
        MedicalRecordShare share = new MedicalRecordShare(
                hash(rawToken),
                PATIENT_EMAIL,
                Instant.now(),
                expiry
        );
        when(shareRepository.findByTokenHashAndRevokedFalseAndExpiresAtAfter(
                eq(hash(rawToken)),
                any(Instant.class)
        )).thenReturn(Optional.of(share));
        when(recordRepository.findByPatientEmailIgnoreCaseOrderByIdDesc(PATIENT_EMAIL))
                .thenReturn(List.of());

        var response = service.getSharedRecords(rawToken);

        assertEquals("Patient", response.patientName());
        assertEquals(expiry, response.expiresAt());
        assertTrue(response.records().isEmpty());
    }

    @Test
    void resolveShareIncludesLinkedRecordsWithoutPatientIdentifiers() throws Exception {
        String rawToken = "active-token";
        Instant expiry = Instant.now().plus(Duration.ofMinutes(5));
        MedicalRecordShare share = new MedicalRecordShare(
                hash(rawToken),
                PATIENT_EMAIL,
                Instant.now(),
                expiry
        );
        when(shareRepository.findByTokenHashAndRevokedFalseAndExpiresAtAfter(
                eq(hash(rawToken)),
                any(Instant.class)
        )).thenReturn(Optional.of(share));
        when(recordRepository.findByPatientEmailIgnoreCaseOrderByIdDesc(PATIENT_EMAIL))
                .thenReturn(List.of(record()));

        var response = service.getSharedRecords(rawToken);

        assertEquals("Patient One", response.patientName());
        assertEquals(1, response.records().size());
        assertEquals("Central Hospital", response.records().get(0).hospital());
        assertFalse(response.records().get(0).toString().contains(PATIENT_EMAIL));
    }

    @Test
    void resolveShareRejectsExpiredOrRevokedToken() throws Exception {
        String rawToken = "expired-token";
        when(shareRepository.findByTokenHashAndRevokedFalseAndExpiresAtAfter(
                eq(hash(rawToken)),
                any(Instant.class)
        )).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.getSharedRecords(rawToken));
        verifyNoInteractions(recordRepository);
    }

    private static User patient() {
        User patient = new User();
        patient.setEmail(PATIENT_EMAIL);
        patient.setRole("PATIENT");
        return patient;
    }

    private static MedicalRecord record() {
        MedicalRecord record = new MedicalRecord();
        record.setPatientName("Patient One");
        record.setPatientEmail(PATIENT_EMAIL);
        record.setHospital("Central Hospital");
        record.setDoctor("Dr. Example");
        record.setDisease("Example diagnosis");
        record.setMedicines("Example medicine");
        record.setVisitDate("2026-01-05");
        record.setNotes("Private clinical notes");
        return record;
    }

    private static String hash(String token) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }
}
