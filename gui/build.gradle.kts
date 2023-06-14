plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}

group = "earth.groundctrl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "20"
    modules("javafx.controls", "javafx.fxml", "javafx.media")
}

dependencies {
    implementation(project(":library"))
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
