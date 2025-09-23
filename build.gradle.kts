plugins {
	kotlin("jvm") version "2.2.0"
	kotlin("plugin.spring") version "2.2.0"
	id("org.springframework.boot") version "4.0.0-M3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "nl.appetit"
version = "0.0.1-SNAPSHOT"
description = "Api for appetit"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    // --- Core Spring Boot + Kotlin ---
	implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

    // --- Swagger ---
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.13")

    // --- Reactive Database (if you want R2DBC) ---
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

	// --- Database Migrations ---
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql:11.13.1")
	implementation("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun>().configureEach {
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}
