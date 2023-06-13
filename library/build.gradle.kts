plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("org.json:json:20230227")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
