package com.musicApp.backend.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * A file that configures the OpenAI service to be usable in the backend, it goes through
 * application.properties and finds the OpenAI API key
 */
@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAIClient openAIClient() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        String apiKey = dotenv.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY missing (check your .env file)");
        }

        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }
}
