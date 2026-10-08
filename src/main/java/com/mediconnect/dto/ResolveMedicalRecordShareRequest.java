package com.mediconnect.dto;

import jakarta.validation.constraints.NotBlank;

public record ResolveMedicalRecordShareRequest(@NotBlank String token) {
}
