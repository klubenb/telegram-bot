plugins {
    java
//	id("org.springframework.boot") version "3.2.5"
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "ru.skillfactory.homework"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-web-services")
    implementation("org.telegram:telegrambots-springboot-longpolling-starter:7.2.1")
    implementation("org.telegram:telegrambots-client:7.2.1")
    implementation("org.apache.commons:commons-lang3")
    compileOnly("org.projectlombok:lombok")
    compileOnly("com.github.xabgesagtx:telegram-spring-boot-starter:0.26")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

//tasks.withType<JavaCompile> {
//	options.compilerArgs.add("-Amapstruct.defaultComponentModel=spring")
//}

tasks.withType<Test> {
    useJUnitPlatform()
}
