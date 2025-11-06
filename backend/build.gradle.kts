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
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("mysql:mysql-connector-java:8.0.33")

	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
	implementation(("io.jsonwebtoken:jjwt-jackson:0.12.6"))

	implementation("io.github.cdimascio:dotenv-java:3.0.0")


	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

// Maven to Gradle

	compile "org.springframework.boot:spring-boot-dependencies:${spring-boot.version}"
	compile "org.springframework.boot:spring-boot-starter-web:*"
	compile "org.projectlombok:lombok:1.18.34"
	compile "se.michaelthelin.spotify:spotify-web-api-java:9.2.0"
	compile "org.slf4j:slf4j-simple:2.0.12"
}

tasks.withType<Test> {
	useJUnitPlatform()
}
