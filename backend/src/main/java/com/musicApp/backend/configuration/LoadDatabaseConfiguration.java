/**
 * Date: September 25, 2025
 * @author Jose Bastidas
 */

 
package com.musicApp.backend.configuration;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.authentication.utils.Encoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration class initializes the database with a default user upon
 * application startup. It ensures that a sample or administrative account
 * exists by checking the user repository and inserting a predefined record if absent.<br>
 * 
 * The configuration uses a CommandLineRunner bean, which executes immediately
 * after the Spring Boot application context is loaded.
 */
@Configuration
public class LoadDatabaseConfiguration {
    private final Encoder encoder;

    /**
     * Constructs the {@code LoadDatabaseConfiguration} with a password encoder dependency.
     *
     * @param encoder the {@link Encoder} utility used for encrypting user passwords before saving them
     */
    public LoadDatabaseConfiguration(Encoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Creates a CommandLineRunner bean that checks if a default user exists in the
     *     repository and adds one if not.
     * @param authenticationUserRepository the repository used to query and persist {@link AuthenticationUser} entities
     * @return a {@link CommandLineRunner} instance that initializes the database with a default user
     */
    @Bean
    public CommandLineRunner initDatabase(AuthenticationUserRepository authenticationUserRepository){
        return args -> {
            if (authenticationUserRepository.findByEmail("jose@example.com").isEmpty()) {
            AuthenticationUser authenticationUser = new AuthenticationUser("Jose", "Test", "testing", "jose@example.com", encoder.encode("jose"));
            authenticationUserRepository.save(authenticationUser);
        }
        };
    }
}