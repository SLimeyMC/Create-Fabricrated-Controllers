plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    forge()
}

val modId: String = rootProject.property("archives_base_name").toString()

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }
}

/**
 * @see: https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html
 * */
val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val developmentForge: Configuration = configurations.getByName("developmentForge")
configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentForge.extendsFrom(configurations["common"])
}

repositories {
    // mavens for Forge-exclusives
    maven { url = uri("https://maven.theillusivec4.top/") } // Curios
    maven { // Create Forge and Registrate Forge
        url = uri("https://maven.tterrag.com/")
        content {
            includeGroup("com.tterrag.registrate")
            includeGroup("com.simibubi.create")
        }
        maven { url = uri("https://thedarkcolour.github.io/KotlinForForge/") }
    }
}

dependencies {
    forge(libs.forge)
    // Remove the next line if you don't want to depend on the API
    modApi(libs.forge.architectury)

    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", configuration = "transformProductionForge")) { isTransitive = false }

    // Create
    modImplementation(libs.forge.create)

    // CC Tweaked
    modRuntimeOnly(libs.forge.computercraft)

    // Controllable
    modRuntimeOnly(libs.forge.controllable)

    // Recipe viewer
    modLocalRuntime(libs.forge.jei)
}

val javaComponent = components.getByName<AdhocComponentWithVariants>("java")
javaComponent.withVariantsFromConfiguration(configurations["sourcesElements"]) {
    skip()
}

tasks {
    processResources {
        inputs.property("version", project.version)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE


        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
        archiveClassifier.set("forge")
    }

    jar {
        archiveClassifier.set("dev")
    }

    sourcesJar {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }



    publishing {
        publications {
            create<MavenPublication>("mavenForge") {
                artifactId = "${rootProject.property("archives_base_name")}-${project.name}"
                from(javaComponent)
            }
        }

        // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
        repositories {
            // Add repositories to publish to here.
        }
    }
}
