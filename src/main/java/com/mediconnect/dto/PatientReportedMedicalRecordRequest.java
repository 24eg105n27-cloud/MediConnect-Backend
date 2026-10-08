package com.mediconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientReportedMedicalRecordRequest(
        @NotBlank @Size(max = 255) String hospital,
        @NotBlank @Size(max = 255) String disease,
        @Size(max = 255) String medicines,
        @NotBlank @Size(max = 255) String visitDate,
        @Size(max = 255) String doctor,
        @Size(max = 1000) String notes
) {
}
