
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(
    "api",
    "core",
    "cli",
)

rootProject.name = "freecam-publish"
