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
		property("sonar.projectKey", "kerekdominik_portfolio-manager")
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

dependencies {
	platform(libs.spring.boot.bom)
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-cache")

	/** Imported and managed manually **/
	implementation(libs.springdoc.openapi.starter.webmvc.ui)
	implementation(libs.jjwt.api)
	implementation(libs.jjwt.impl)
	implementation(libs.jjwt.jackson)
	implementation(libs.mapstruct)
	implementation(libs.caffeine)
	/**********************************/

	runtimeOnly("org.postgresql:postgresql")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor(libs.mapstruct.processor)
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.h2database:h2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}


/** Coverage and test report **/
tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

jacoco {
	toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}
/*********************************/