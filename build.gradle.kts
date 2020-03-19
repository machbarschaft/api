import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.5.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.70"
	kotlin("plugin.spring") version "1.3.70"
	kotlin("plugin.jpa") version "1.3.70"
}

description = "Service to provide the colivery API"
group = "app.colivery"
java.sourceCompatibility = JavaVersion.VERSION_11

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
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.8.6")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.hibernate.validator:hibernate-validator:6.1.2.Final")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

fun Project.getKtlintConfiguration(): Configuration {
    return configurations.findByName("ktlint") ?: configurations.create("ktlint") {
        val dependency = project.dependencies.create("com.pinterest:ktlint:0.36.0")
        dependencies.add(dependency)
    }
}

tasks.register<JavaExec>(name = "ktlint") {
    group = "ktlint"
    description = "Check Kotlin code style."
    main = "com.pinterest.ktlint.Main"
    classpath = getKtlintConfiguration()
    args("src/**/*.kt")
}
tasks.check {
    dependsOn("ktlint")
}

tasks.register<JavaExec>(name = "ktlintFormat") {
    group = "ktlint"
    description = "Fix Kotlin code style deviations."
    main = "com.pinterest.ktlint.Main"
    classpath = getKtlintConfiguration()
    args("-F", "src/**/*.kt")
}

tasks.register<JavaExec>(name = "ktlintIntellij") {
    group = "ktlint"
    description = "Setup IntelliJ KTLint configuration."
    classpath = getKtlintConfiguration()
    main = "com.pinterest.ktlint.Main"
    args("--apply-to-idea-project", "-y")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar>().configureEach {
    enabled = true
    manifest {
        attributes(
            "Implementation-Title" to project.description,
            "Implementation-Version" to project.version
        )
    }
}
