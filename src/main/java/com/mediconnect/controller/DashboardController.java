package com.mediconnect.controller;

import com.mediconnect.dto.DashboardStatsResponse;
import com.mediconnect.model.Appointment;
import com.mediconnect.repository.AppointmentRepository;
import com.mediconnect.repository.DoctorRepository;
import com.mediconnect.repository.MedicalRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public DashboardController(AppointmentRepository appointmentRepository,
                               DoctorRepository doctorRepository,
                               MedicalRecordRepository medicalRecordRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        long totalAppointments = allAppointments.size();

        LocalDate today = LocalDate.now();
        long upcomingVisits = allAppointments.stream()
                .filter(a -> !"Cancelled".equalsIgnoreCase(a.getStatus()))
                .filter(a -> {
                    try {
                        return a.getDate() != null && !LocalDate.parse(a.getDate()).isBefore(today);
                    } catch (Exception e) {
                        return true;
                    }
                })
                .count();

        long totalDoctors = doctorRepository.count();
        long totalRecords = medicalRecordRepository.count();

        return ResponseEntity.ok(new DashboardStatsResponse(
                totalAppointments,
                upcomingVisits,
                totalDoctors,
                totalRecords
        ));
    }
}
