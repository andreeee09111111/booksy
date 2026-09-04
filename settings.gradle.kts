pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.5.0"
        id("org.jetbrains.kotlin.android") version "1.9.24"
        id("org.jetbrains.kotlin.kapt") version "1.9.24"   // ← Necesario para el plugin kapt
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "booksy66"
include(":app")