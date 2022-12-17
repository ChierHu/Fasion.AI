import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":vesta"))

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.1.4")
    implementation("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("io.lettuce:lettuce-core")
    implementation("org.apache.commons:commons-pool2")

    implementation("com.ksyun:ks3-kss-java-sdk:1.0.2")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

//    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}