import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

tasks.withType<BootJar> {
    enabled = false
}

tasks.withType<Jar> {
    enabled = true
}

noArg {
    annotation("ai.fasion.fabs.vesta.NoArg")
}

val slf4jVersion: String by rootProject.extra

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

//    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.1.4")
    implementation("org.postgresql:postgresql")

    implementation("io.springfox:springfox-boot-starter:3.0.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("org.hashids:hashids:1.0.3")
    implementation("commons-codec:commons-codec:1.12")
    implementation("joda-time:joda-time:2.8.1")
    implementation("org.jodd:jodd-http:3.9.1")
    implementation("commons-fileupload:commons-fileupload:1.3.1")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("com.ksyun:ks3-kss-java-sdk:1.0.2")

}