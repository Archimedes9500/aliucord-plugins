@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven {
            name = "aliucord"
            url = uri("https://maven.aliucord.com/releases")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "aliucord"
            url = uri("https://maven.aliucord.com/releases")
        }
    }
}

rootProject.name = "aliucord-plugins"
include(":plugin")

// Add each directory under ./plugin as a separate project
rootDir.resolve("plugin")
    .listFiles { file -> file.isDirectory && file.resolve("build.gradle.kts").exists() }!!
    .forEach { include(":plugin:${it.name}") }
