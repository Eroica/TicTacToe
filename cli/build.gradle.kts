plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "earth.groundctrl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library"))
    implementation("org.json:json:20230227")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(19)
}
