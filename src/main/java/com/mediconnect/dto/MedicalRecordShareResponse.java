package com.mediconnect.dto;

import java.time.Instant;

public record MedicalRecordShareResponse(String token, Instant expiresAt, int recordCount) {
}
