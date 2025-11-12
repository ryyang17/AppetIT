import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
	alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
	id("jacoco")
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
	implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.webflux)
	implementation(libs.kotlin.reflect)
	// --- Hot-reload ---
	developmentOnly(libs.spring.boot.devtools)
    // --- Swagger ---
    implementation(libs.springdoc.openapi.starter.webflux)

    // --- Reactive Database (if you want R2DBC) ---
    implementation(libs.spring.boot.starter.data.r2dbc)
    runtimeOnly(libs.r2dbc.postgresql)

	// --- Database Migrations ---
	implementation(libs.flyway.core)
	implementation(libs.flyway.database.postgresql)
	implementation(libs.postgresql)

	testImplementation(libs.spring.boot.starter.webflux.test)
	testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
	testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

val commitHash = System.getenv("COMMIT_HASH") ?: "latest"
val deploymentType = System.getenv("DEPLOYMENT_TYPE") ?: "staging"

tasks.named<BootBuildImage>("bootBuildImage") {
	imageName.set("547988/appetit-api:${commitHash}")
	publish.set(true)
	docker {
		publishRegistry {
			username.set(System.getenv("DOCKER_USERNAME"))
			password.set(System.getenv("DOCKER_PASSWORD"))
		}
	}
	tags.set(setOf("547988/appetit-api:${deploymentType}"))
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
	jvmArgs = listOf(
		"-javaagent:${classpath.find { it.name.contains("mockito-core") }?.absolutePath}"
	)
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun>().configureEach {
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}
