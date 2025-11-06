package com.musicApp.backend.features.authentication.repository;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthenticationUserRepository extends JpaRepository<AuthenticationUser, Long> {
    Optional<AuthenticationUser> findByEmail(String email);

    Optional<AuthenticationUser> findByUsername(String username);

    // Find all currently logged-in users
    List<AuthenticationUser> findByLoginStatusTrue();
}

