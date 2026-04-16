/**
 * Class Name: R2Config
 * Date: February 13, 2026
 * @author Jose Bastidas
 */
package com.musicApp.backend.profiles.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * This class configures the Cloudflare R2 presigner used by the application.
 * It creates an S3Presigner bean with the required credentials, region,
 * and endpoint settings so the application can generate presigned URLs.
 */
@Configuration
public class R2Config {

    /**
     * Creates and returns an S3Presigner for Cloudflare R2 storage.
     *
     * @param accessKeyId the access key ID for the R2 account
     * @param secretAccessKey the secret access key for the R2 account
     * @param accountId the Cloudflare account ID used to build the R2 endpoint
     * @return the configured {@link S3Presigner} object
     */
    @Bean
    public S3Presigner r2Presigner(
            @Value("${r2.accessKeyId}") String accessKeyId,
            @Value("${r2.secretAccessKey}") String secretAccessKey,
            @Value("${r2.accountId}") String accountId
    ) {
        var credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("https://" + accountId + ".r2.cloudflarestorage.com"))
                .build();
    }
}