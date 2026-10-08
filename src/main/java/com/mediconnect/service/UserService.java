package com.mediconnect.service;

import com.mediconnect.dto.AuthResponse;
import com.mediconnect.dto.LoginRequest;
import com.mediconnect.dto.RegisterRequest;
import com.mediconnect.model.User;
import com.mediconnect.repository.UserRepository;
import com.mediconnect.repository.AuthSessionRepository;
import com.mediconnect.model.AuthSession;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthSessionRepository authSessionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, AuthSessionRepository authSessionRepository) {
        this.userRepository = userRepository;
        this.authSessionRepository = authSessionRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(false, "Email is already registered!", null);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("PATIENT");

        User savedUser = userRepository.save(user);
        return new AuthResponse(true, "Registration successful!", savedUser, createSession(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return new AuthResponse(false, "User not found with this email!", null);
        }

        User user = userOptional.get();
        String storedPassword = user.getPassword();
        boolean encodedPassword = isBcryptHash(storedPassword);
        boolean passwordMatches = encodedPassword
                ? passwordEncoder.matches(request.getPassword(), storedPassword)
                : storedPassword.equals(request.getPassword());
        if (!passwordMatches) {
            return new AuthResponse(false, "Invalid password!", null);
        }

        if (!encodedPassword) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
        }
        String sessionToken = createSession(user);
        return new AuthResponse(true, "Login successful!", user, sessionToken);
    }

    public Optional<User> getProfileByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateProfile(User updatedData) {
        Optional<User> existingOptional = userRepository.findByEmail(updatedData.getEmail());
        if (existingOptional.isPresent()) {
            User existing = existingOptional.get();
            if (updatedData.getName() != null) existing.setName(updatedData.getName());
            if (updatedData.getPhone() != null) existing.setPhone(updatedData.getPhone());
            if (updatedData.getAge() != null) existing.setAge(updatedData.getAge());
            if (updatedData.getGender() != null) existing.setGender(updatedData.getGender());
            return userRepository.save(existing);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated account no longer exists");
    }

    private String createSession(User user) {
        byte[] tokenBytes = new byte[32];
        new java.security.SecureRandom().nextBytes(tokenBytes);
        String rawToken = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        String tokenHash = hashToken(rawToken);
        authSessionRepository.save(new AuthSession(
                tokenHash,
                user,
                java.time.Instant.now().plus(java.time.Duration.ofHours(12))
        ));
        return rawToken;
    }

    public Optional<User> authenticate(String rawToken) {
        return authSessionRepository
                .findByTokenHashAndRevokedFalseAndExpiresAtAfter(hashToken(rawToken), java.time.Instant.now())
                .map(AuthSession::getUser);
    }

    public void revokeSession(String rawToken) {
        authSessionRepository.findByTokenHash(hashToken(rawToken)).ifPresent(session -> {
            session.setRevoked(true);
            authSessionRepository.save(session);
        });
    }

    private String hashToken(String rawToken) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private boolean isBcryptHash(String value) {
        return value != null && value.matches("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");
    }
}
