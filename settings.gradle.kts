
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
    repositories {
        exclusiveContent {
            forRepository {
                maven("https://jitpack.io") { name = "JitPack" }
            }
            filter {
                includeModule("com.github.masecla22", "Modrinth4J")
            }
        }
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
