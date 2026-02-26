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
    implementation(project(":platforms"))
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
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
