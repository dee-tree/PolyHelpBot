plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.techproj.polyhelpbot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
    implementation("dev.inmo:micro_utils.fsm.common:0.10.5")


    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    implementation(kotlin("reflect"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}