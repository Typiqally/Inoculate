plugins {
    java
}

group = "inoculate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("org.spigotmc", "spigot", "1.15.2-R0.1-SNAPSHOT")
    implementation("org.ow2.asm", "asm-debug-all", "5.2")
    implementation("commons-io", "commons-io", "2.6")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}