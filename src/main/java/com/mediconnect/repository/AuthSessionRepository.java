package com.mediconnect.repository;

import com.mediconnect.model.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {
    Optional<AuthSession> findByTokenHashAndRevokedFalseAndExpiresAtAfter(String tokenHash, Instant now);
    Optional<AuthSession> findByTokenHash(String tokenHash);
}
