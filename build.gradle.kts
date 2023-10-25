plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "com.cs203g3"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-mail")

	implementation("com.google.code.gson:gson:2.10.1")
	implementation("com.stripe:stripe-java:23.6.0")
	implementation("org.thymeleaf:thymeleaf:3.1.2.RELEASE")
	implementation("org.thymeleaf:thymeleaf-spring5:3.1.2.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.1.2")
	implementation("org.xhtmlrenderer:flying-saucer-pdf:9.3.1")
	implementation("org.modelmapper:modelmapper:3.1.1")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	testImplementation("com.h2database:h2:2.2.224")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootRun {
	environment("spring.profiles.active", "dev")
}
