package com.mediconnect.repository;

import com.mediconnect.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientEmailOrderByDateAscTimeAsc(String patientEmail);
    List<Appointment> findByPatientNameIgnoreCaseOrderByDateAscTimeAsc(String patientName);
    List<Appointment> findAllByOrderByDateAscTimeAsc();
}
