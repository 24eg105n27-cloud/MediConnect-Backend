package com.mediconnect.dto;

import java.time.Instant;
import java.util.List;

public record SharedMedicalRecordsResponse(
        String patientName,
        Instant expiresAt,
        List<SharedMedicalRecord> records
) {
}
