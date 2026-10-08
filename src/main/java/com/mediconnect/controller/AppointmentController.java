package com.mediconnect.controller;

import com.mediconnect.model.Appointment;
import com.mediconnect.repository.AppointmentRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;

    public AppointmentController(
            AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // Get all appointments
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByOrderByDateAscTimeAsc();
    }

    // Get appointments for a particular patient using email
    @GetMapping("/patient")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(
            @RequestParam("email") String email) {

        List<Appointment> appointments =
                appointmentRepository
                        .findByPatientEmailOrderByDateAscTimeAsc(email);

        return ResponseEntity.ok(appointments);
    }

    // Create new appointment
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Appointment appointment) {

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return ResponseEntity.ok(savedAppointment);
    }
}