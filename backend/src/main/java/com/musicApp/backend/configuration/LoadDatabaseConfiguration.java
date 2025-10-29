package com.musicApp.backend.configuration;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.authentication.utils.Encoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabaseConfiguration {
    private final Encoder encoder;

    public LoadDatabaseConfiguration(Encoder encoder) {
        this.encoder = encoder;
    }

    @Bean
    public CommandLineRunner initDatabase(AuthenticationUserRepository authenticationUserRepository){
        return args -> {
            if (authenticationUserRepository.findByEmail("jose@example.com").isEmpty()) {
            AuthenticationUser authenticationUser = new AuthenticationUser("Jose", "Test", "test1", "jose@example.com", encoder.encode("jose"));
            authenticationUserRepository.save(authenticationUser);
        }
        };
    }
}