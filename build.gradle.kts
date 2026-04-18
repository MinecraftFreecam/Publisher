plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

version = property("version").toString()
group = property("group").toString()

application {
    mainClass = "net.xolt.freecam.publish.MainKt"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":cli"))
    implementation(project(":curseforge"))
    implementation(project(":modrinth"))
    implementation(libs.kotlin.coroutines)
    testImplementation(libs.kotlin.test)
    testImplementation(testFixtures(project(":api")))
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockk)
}

tasks {
    jar {
        from("LICENSE")
    }

    processResources {
        sequenceOf(
            "version",
            "api_version",
        ).associateWith { key ->
            project.properties[key].toString()
        }.also { buildProperties ->
            inputs.properties(buildProperties)
            filesMatching("build.properties") { expand(buildProperties) }
        }
    }

    check {
        // Also run subproject tests
        dependsOn(provider {
            subprojects.mapNotNull { it.tasks.findByName("check") }
        })
    }

    test {
        useJUnitPlatform()

        // Also run subproject tests
        dependsOn(provider {
            subprojects.mapNotNull { it.tasks.findByName("test") }
        })
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}
