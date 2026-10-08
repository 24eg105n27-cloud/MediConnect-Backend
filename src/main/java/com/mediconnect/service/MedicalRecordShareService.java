package com.mediconnect.service;

import com.mediconnect.dto.MedicalRecordShareResponse;
import com.mediconnect.dto.MedicalRecordShareStatusResponse;
import com.mediconnect.dto.SharedMedicalRecord;
import com.mediconnect.dto.SharedMedicalRecordsResponse;
import com.mediconnect.model.MedicalRecord;
import com.mediconnect.model.MedicalRecordShare;
import com.mediconnect.model.User;
import com.mediconnect.repository.MedicalRecordRepository;
import com.mediconnect.repository.MedicalRecordShareRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
public class MedicalRecordShareService {

    private static final Duration SHARE_LIFETIME = Duration.ofMinutes(15);

    private final MedicalRecordShareRepository shareRepository;
    private final MedicalRecordRepository recordRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public MedicalRecordShareService(
            MedicalRecordShareRepository shareRepository,
            MedicalRecordRepository recordRepository
    ) {
        this.shareRepository = shareRepository;
        this.recordRepository = recordRepository;
    }

    public MedicalRecordShareResponse createShare(User patient) {
        if (!"PATIENT".equalsIgnoreCase(patient.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patients can create a records share");
        }

        var records = recordRepository.findByPatientEmailIgnoreCaseOrderByIdDesc(patient.getEmail());
        revokeShares(patient.getEmail());
        Instant now = Instant.now();
        String rawToken = createOpaqueToken();
        shareRepository.save(new MedicalRecordShare(
                hashToken(rawToken),
                patient.getEmail(),
                now,
                now.plus(SHARE_LIFETIME)
        ));
        return new MedicalRecordShareResponse(rawToken, now.plus(SHARE_LIFETIME), records.size());
    }

    public void revokeShares(String patientEmail) {
        shareRepository.findByPatientEmailIgnoreCaseAndRevokedFalse(patientEmail).forEach(share -> {
            share.setRevoked(true);
            shareRepository.save(share);
        });
    }

    public MedicalRecordShareStatusResponse getShareStatus(String patientEmail) {
        return shareRepository
                .findTopByPatientEmailIgnoreCaseAndRevokedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        patientEmail,
                        Instant.now()
                )
                .map(share -> new MedicalRecordShareStatusResponse(true, share.getExpiresAt()))
                .orElseGet(() -> new MedicalRecordShareStatusResponse(false, null));
    }

    public SharedMedicalRecordsResponse getSharedRecords(String rawToken) {
        MedicalRecordShare share = shareRepository
                .findByTokenHashAndRevokedFalseAndExpiresAtAfter(hashToken(rawToken), Instant.now())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "This medical-record share has expired or been revoked"
                ));

        var records = recordRepository.findByPatientEmailIgnoreCaseOrderByIdDesc(share.getPatientEmail());

        return new SharedMedicalRecordsResponse(
                records.isEmpty() ? "Patient" : records.get(0).getPatientName(),
                share.getExpiresAt(),
                records.stream().map(SharedMedicalRecord::from).toList()
        );
    }

    private String createOpaqueToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
