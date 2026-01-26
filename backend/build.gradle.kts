import org.gradle.external.javadoc.StandardJavadocDocletOptions

plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.musicApp"
version = "0.0.1-SNAPSHOT"
description = "Backend for musicApp"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // --- Spring Boot core ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // --- Database ---
    implementation("mysql:mysql-connector-java:8.0.33")

    // --- JWT Authentication ---
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // --- Environment Variables ---
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    // --- From your old pom.xml ---
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    implementation("se.michaelthelin.spotify:spotify-web-api-java:9.2.0")
   // implementation("org.slf4j:slf4j-simple:2.0.12")

    // --- Dev & Test ---
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //new
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")


    //test
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    //Amazon / Cloudflare R2
    implementation("software.amazon.awssdk:s3:2.41.13")
    implementation("software.amazon.awssdk:url-connection-client:2.41.13")


}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).charSet = "UTF-8"
}

tasks.javadoc {
    destinationDir = file("C:/Users/16617/Desktop/JavaDoc490/JavadocOutput")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}
