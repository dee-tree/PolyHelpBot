import java.util.Properties

plugins {
    kotlin("jvm")
    application
    kotlin("plugin.serialization")
}

group = "com.techproj"
version = "1.0-SNAPSHOT"

val props = Properties()
val secretPropertiesFile = File(rootDir, "settings.properties")
require(secretPropertiesFile.exists()) { "File settings.properties must exist!" }
secretPropertiesFile.inputStream().let { props.load(it) }


repositories {
    mavenCentral()
}

val botMainClassName = "com.techproj.polyhelpbot.MainKt"

application {
    mainClass.set(botMainClassName)
}

val tgbotapiVersion = "2.0.0"

dependencies {

    implementation("dev.inmo:tgbotapi:$tgbotapiVersion")

    implementation(project(":db"))
    implementation(project(":core"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.create<Jar>("fatJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = botMainClassName
    }

    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks.getByName<JavaExec>("run") {
    dependsOn("fatJar")
    args(
        props["BOT_API_KEY"],
        props["DATABASE_CONFIGURATION"]
    )
}