package com.Mediconnect.backend.controller;

import com.Mediconnect.backend.entity.MedicalRecord;
import com.Mediconnect.backend.repository.MedicalRecordRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "http://localhost:5173")
public class MedicalRecordController {

    private final MedicalRecordRepository repository;

    public MedicalRecordController(MedicalRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<MedicalRecord> getAllRecords() {
        return repository.findAll();
    }

    @PostMapping
    public MedicalRecord addRecord(@RequestBody MedicalRecord record) {
        return repository.save(record);
    }
}