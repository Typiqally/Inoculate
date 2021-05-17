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
    implementation(project(":inoculate-injector"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        val classPaths = configurations.runtimeClasspath.get()
        val filteredClassPaths = classPaths.filter { !it.name.contains("spigot") }.map { if (it.isDirectory) it else zipTree(it) };

        from(filteredClassPaths)
        from(sourceSets.main.get().output)
    }
}