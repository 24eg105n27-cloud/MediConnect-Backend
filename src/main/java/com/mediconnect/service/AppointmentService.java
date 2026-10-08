package com.mediconnect.service;

import com.mediconnect.model.Appointment;
import com.mediconnect.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByOrderByDateAscTimeAsc();
    }

    public List<Appointment> getAppointmentsByEmail(String email) {
        return appointmentRepository.findByPatientEmailOrderByDateAscTimeAsc(email);
    }

    public List<Appointment> getAppointmentsByPatientName(String name) {
        return appointmentRepository.findByPatientNameIgnoreCaseOrderByDateAscTimeAsc(name);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment bookAppointment(Appointment appointment) {
        if (appointment.getStatus() == null || appointment.getStatus().isBlank()) {
            appointment.setStatus("Confirmed");
        }
        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> cancelAppointment(Long id) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setStatus("Cancelled");
            return appointmentRepository.save(appointment);
        });
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public long countTotal() {
        return appointmentRepository.count();
    }
}
