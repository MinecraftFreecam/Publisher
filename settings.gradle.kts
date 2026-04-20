
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
    repositories {
        exclusiveContent {
            forRepository {
                maven("https://maven.firstdark.dev/releases") { name = "First Dark Development" }
            }
            filter {
                includeGroupAndSubgroups("me.hypherionmc")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

include(
    "api",
    "core",
    "cli",
    "curseforge"
)

rootProject.name = "freecam-publish"
