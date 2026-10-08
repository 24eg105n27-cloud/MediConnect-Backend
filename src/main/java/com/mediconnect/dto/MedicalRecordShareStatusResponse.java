package com.mediconnect.dto;

import java.time.Instant;

public record MedicalRecordShareStatusResponse(boolean active, Instant expiresAt) {
}
