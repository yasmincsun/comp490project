 /**
 * Date: September 25, 2025
 * @author Jose Bastidas
 */

package com.musicApp.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import io.github.cdimascio.dotenv.Dotenv;


/**
 * Entry point for the MusicApp backend application built with Spring Boot.
 * This class initializes the application context, loads environment variables
 * from a .env file, and configures essential beans like RestTemplate for HTTP operations.
 */
@SpringBootApplication
public class BackendApplication {
  
	/**
	 * Loads environment variables and launches the Spring Boot application.
   * @param args Command-line arguments passed to the application
	*/
	public static void main(String[] args) {

		 Dotenv dotenv = Dotenv.load();

        // Inject .env values into system properties for Spring Boot
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("TRUSTSTORE_PASSWORD", dotenv.get("TRUSTSTORE_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
				System.setProperty("SPOTIFY_CLIENT_ID", dotenv.get("SPOTIFY_CLIENT_ID"));
				System.setProperty("SPOTIFY_CLIENT_SECRET", dotenv.get("SPOTIFY_CLIENT_SECRET"));

		SpringApplication.run(BackendApplication.class, args);
	}

	/**
	 *	Declares a RestTemplate bean for performing RESTful HTTP requests.
   * @return RestTemplate instance used for making HTTP calls to external services
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


}
