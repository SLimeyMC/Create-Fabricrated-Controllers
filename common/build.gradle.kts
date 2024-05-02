import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
}

architectury {
    val enabled_platforms: String by rootProject
    common(enabled_platforms.split(","))
}

repositories {
    mavenCentral()
    // mavens for Create Fabric and dependencies
    maven { url = uri("https://api.modrinth.com/maven") }
    maven { url = uri("https://mvn.devos.one/snapshots/") } // Create Fabric, Porting Lib, Forge Tags, Milk Lib, Registrate Fabric
    maven { url = uri("https://cursemaven.com") }
    maven { url = uri("https://maven.cafeteria.dev/releases") } // Fake Player API
    maven { url = uri("https://maven.jamieswhiteshirt.com/libs-release") } // Reach Entity Attributes
    maven { url = uri("https://jitpack.io/") } // Mixin Extras, Fabric ASM
}

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation(libs.fabric.loader)
    // Remove the next line if you don't want to depend on the API
    modApi(libs.common.architectury)

    // Using Create Fabric
    modCompileOnly(libs.fabric.create)

    // CC Restitched
    modCompileOnly(libs.fabric.computercraft)
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = rootProject.property("archives_base_name").toString()
            from(components.getByName("java"))
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
    }
}
