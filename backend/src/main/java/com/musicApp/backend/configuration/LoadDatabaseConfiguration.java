package com.musicApp.backend.configuration;

import com.musicApp.backend.features.databasemodel.*;
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
    // This method initializes the database with some default data at application startup
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            MoodRepository moodRepository,
            PlaylistRepository playlistRepository,
            SongRepository songRepository,
            ReviewRepository reviewRepository
            //add other repositories here if needed
    ) {
        return args -> {
            // Initialize some moods and users for testing
            Mood happy = new Mood();
            happy.setMajorMood("Happy");
            moodRepository.save(happy);
            //Test user
            User user = new User();
            user.setUsername("exampleUser");
            user.setPassword(encoder.encode("test"));
            user.setCurrentMood(happy);
            userRepository.save(user);
            //Returns a message to the console when the database is initialized
            System.out.println("Database initialized.");
        };
    }
}
