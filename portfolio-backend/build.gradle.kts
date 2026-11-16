plugins {
	java
	jacoco
	alias(libs.plugins.springframework.boot)
	alias(libs.plugins.spring.dependency.management)
	alias(libs.plugins.sonarqube)
}

group = "com.portfolio"
version = "1.0.0"

sonar {
	properties {
		property("sonar.projectKey", "portfolio-manager-backend")
		property("sonar.projectName", "portfolio-manager-backend")
		property("sonar.organization", "kerekdominik")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.java.coveragePlugin", "jacoco")
		property("sonar.jacoco.reportPaths", "build/jacoco/test.exec")
	}
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations.compileOnly {
	extendsFrom(configurations.annotationProcessor.get())
}

repositories {
	mavenCentral()
}

/** Pin every resolved dependency version in gradle.lockfile (update with: ./gradlew dependencies --write-locks) **/
dependencyLocking {
	lockAllConfigurations()
}

dependencies {
	platform(libs.spring.boot.bom)
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	/** Imported and managed manually **/
	implementation(libs.springdoc.openapi.starter.webmvc.ui)
	implementation(libs.jjwt.api)
	implementation(libs.jjwt.impl)
	implementation(libs.jjwt.jackson)
	implementation(libs.mapstruct)
	implementation(libs.caffeine)
	/**********************************/

	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.flywaydb:flyway-database-postgresql")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor(libs.mapstruct.processor)
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.h2database:h2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}


/** Packaging: one executable jar with a fixed name, so the Dockerfile does not depend on the version **/
tasks.jar {
	enabled = false // no "-plain.jar" next to the boot jar
}

tasks.bootJar {
	archiveFileName.set("app.jar")
}

/** Coverage and test report **/
tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

jacoco {
	toolVersion = "0.8.14"
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}
/*********************************/
