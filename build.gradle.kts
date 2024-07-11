plugins {
    kotlin("jvm") version "2.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(libs.bundles.ktor.client)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}