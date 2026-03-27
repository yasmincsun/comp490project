package com.musicApp.backend.profiles.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;


@Configuration
public class R2Config{

  @Bean
  public S3Presigner r2Presigner(
    @Value("${r2.accessKeyId}") String accessKeyId,
    @Value("${r2.secretAccessKey}") String secretAccessKey,
    @Value("${r2.accountId}") String accountId
  ){
    var credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
     
    return S3Presigner.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.US_EAST_1)
        .endpointOverride(URI.create("https://" + accountId + ".r2.cloudflarestorage.com"))
        .build();
  }
  
}
