import java.util.Properties


plugins {
    kotlin("jvm")
    application
    kotlin("plugin.serialization")

}

group = "com.techproj.polyhelpbot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:0.38.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.38.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.38.2")

    implementation("com.h2database:h2:2.1.212")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    // string utils such as Levenshtein distance
    implementation("org.apache.commons:commons-text:1.9")

    implementation("dev.inmo:micro_utils.fsm.common:0.10.5")


    implementation(project(":core"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

val props = Properties()

val secretSettingsFile = File(rootDir, "settings.properties")
require(secretSettingsFile.exists()) { "File settings.properties must exist!" }
secretSettingsFile.inputStream().let { props.load(it) }

val mainClassFqName = "com.techproj.polyhelpbot.dbinit.MainKt"

application {
    mainClass.set(mainClassFqName)
}

tasks.getByName<JavaExec>("run") {
    args(
        props["DATABASE_CONFIGURATION"],
        props["DATABASE_INITIAL_CONFIG_FILE"],
    )
}