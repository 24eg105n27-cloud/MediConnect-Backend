package com.mediconnect.config;

import com.mediconnect.model.Doctor;
import com.mediconnect.model.User;
import com.mediconnect.repository.DoctorRepository;
import com.mediconnect.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DataLoader(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Seed default doctors if none exist
        if (doctorRepository.count() == 0) {
            doctorRepository.saveAll(List.of(
                    new Doctor("Dr. Anjali Sharma", "Cardiologist", "Apollo Hospital", "10 years"),
                    new Doctor("Dr. Rahul Kumar", "Dermatologist", "Yashoda Hospital", "8 years"),
                    new Doctor("Dr. Priya Reddy", "General Physician", "KIMS Hospital", "7 years"),
                    new Doctor("Dr. Arjun Rao", "Neurologist", "CARE Hospital", "12 years")
            ));
            System.out.println(">>> Initial doctors seeded successfully.");
        }

        // Seed demo user if none exists
        if (userRepository.count() == 0) {
            User demoUser = new User("Siri", "siri@example.com", "password123");
            demoUser.setPhone("9876543210");
            demoUser.setAge(24);
            demoUser.setGender("Female");
            userRepository.save(demoUser);
            System.out.println(">>> Demo user seeded successfully (siri@example.com / password123).");
        }
    }
}
