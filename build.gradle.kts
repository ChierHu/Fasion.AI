import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-noarg:1.5.10")
        classpath("org.jetbrains.kotlin:kotlin-allopen:1.5.10")
    }
}

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.5.10" // apply false

    id("org.springframework.boot") version "2.3.11.RELEASE" // apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" // apply false

    kotlin("plugin.spring") version "1.5.10" // apply false
    kotlin("plugin.noarg") version "1.5.10" // apply false
}

val profile = "develop"
val versionName = "0.2.0-alpha.1"

val dockerRegistry by extra("registry.cn-beijing.aliyuncs.com")
val slf4jVersion by extra("1.7.25")


allprojects {
    group = "ai.fasion.fabs"
    version = versionName

    repositories {
        mavenCentral(); google(); jcenter()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.kapt")
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> { useJUnitPlatform() }

    tasks.withType<ProcessResources> {
        filesMatching("*.yml") {
            expand("profile" to profile, "versionName" to versionName)
        }
    }

    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
        environment = mapOf("BP_JVM_VERSION" to "8.*")
    }

}

