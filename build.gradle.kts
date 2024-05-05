import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("architectury-plugin") version "3.+"
    id("dev.architectury.loom") version "1.4.+" apply false
    kotlin("jvm") version "1.9.22" apply false
}

architectury {
    minecraft = rootProject.property("minecraft_version").toString()
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.shedaniel.me/") } // Cloth Config
        maven { url = uri("https://dvs1.progwml6.com/files/maven/") } // JEI
        maven { url = uri("https://maven.parchmentmc.org") } // Parchment Mapping
        maven { // Flywheel
            url = uri("https://maven.tterrag.com/")
            content {
                // need to be specific here due to version overlaps
                includeGroup("com.jozufozu.flywheel")
            }
        }
    }

    dependencies {
        "minecraft"(libs.minecraft)
        // The following line declares the mojmap mappings, you may use other mappings as well
        //loom.silentMojangMappingsLicense()
        "mappings"( loom.officialMojangMappings() )
        // The following line declares the yarn mappings you may select this one as well.
        // "mappings"("net.fabricmc:yarn:1.19.2+build.3:v2")
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    archivesBaseName = rootProject.property("archives_base_name").toString()
    version = rootProject.property("version").toString()
    group = rootProject.property("maven_group").toString()

    // Formats the mod version to include the loader, Minecraft version, and build number (if present)
    // example: 1.0.0+fabric-1.18.2-100
    val buildNumber = System.getenv("GITHUB_RUN_NUMBER")
    version = rootProject.property("version").toString() + "+" + rootProject.property("name").toString() + "-" + rootProject.property("minecraft_version").toString() + if(buildNumber != null)  "-${buildNumber}" else ""

    repositories {
        // Add repositories to retrieve artifacts from in here.
        // You should only use this when depending on other mods because
        // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
        // See https://docs.gradle.org/current/userguide/declaring_repositories.html
        // for more information about repositories.
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java {
        withSourcesJar()
    }

    val compileKotlin: KotlinCompile by tasks
    compileKotlin.kotlinOptions {
        jvmTarget = "17"
    }
    val compileTestKotlin: KotlinCompile by tasks
    compileTestKotlin.kotlinOptions {
        jvmTarget = "17"
    }
}
