/**
 * Class Name: LoadDatabaseConfiguration
 * Package: com.musicApp.backend.configuration
 * Date: November 10, 2025
 * @author Jose Bastidas
 *
 * Data Structures:
 * - AuthenticationUser: Represents a user entity with fields such as first name,
 *   last name, username, email, and password.
 * - AuthenticationUserRepository: A Spring Data JPA repository for performing CRUD
 *   operations on AuthenticationUser objects.
 *
 * Algorithms:
 * - Simple conditional check: Uses a query (findByEmail) to determine whether a
 *   specific user record already exists. If it doesn’t, a new AuthenticationUser
 *   object is created and persisted.
 *   This approach was chosen for simplicity and efficiency since the dataset is small
 *   and initialization happens only once at startup.
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
     * <p>
     *  Input: AuthenticationUserRepository (repository for user entities)
     * <p>
     *  Output: CommandLineRunner (executed automatically at startup)
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