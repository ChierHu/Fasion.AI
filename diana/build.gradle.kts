import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":vesta"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    kapt("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.1.4")
    implementation("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("io.lettuce:lettuce-core")
    implementation("org.apache.commons:commons-pool2")
    implementation("com.esotericsoftware:kryo:4.0.0")
    implementation("de.javakaffee:kryo-serializers:0.41")

    implementation("com.alibaba:druid-spring-boot-starter:1.1.22")
    implementation("com.github.pagehelper:pagehelper-spring-boot-starter:1.2.10")
    implementation("io.springfox:springfox-boot-starter:3.0.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.apache.commons:commons-lang3:3.8")

    implementation("com.ksyun:ks3-kss-java-sdk:1.0.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}