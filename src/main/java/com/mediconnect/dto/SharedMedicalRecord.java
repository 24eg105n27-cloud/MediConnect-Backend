package com.mediconnect.dto;

import com.mediconnect.model.MedicalRecord;

public record SharedMedicalRecord(
        String hospital,
        String doctor,
        String disease,
        String medicines,
        String visitDate,
        String notes,
        String recordSource
) {
    public static SharedMedicalRecord from(MedicalRecord record) {
        return new SharedMedicalRecord(
                record.getHospital(),
                record.getDoctor(),
                record.getDisease(),
                record.getMedicines(),
                record.getVisitDate(),
                record.getNotes(),
                record.getRecordSource()
        );
    }
}
