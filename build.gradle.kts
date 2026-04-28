plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
}

group = "org.example"
version = "9.4.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))  // ← was 6.0.0, doesn't exist
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.21.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    manifest {
        attributes("Main-Class" to "io.github.markovolimango.logo.Main")
    }
    archiveClassifier.set("")  // names it myproject-9.4.0.jar instead of myproject-9.4.0-all.jar
}