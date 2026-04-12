
tasks {
    register("check") {
        group = "verification"

        // Also run subproject tests
        dependsOn(provider {
            subprojects.mapNotNull { it.tasks.findByName("check") }
        })
    }

    register("test") {
        group = "verification"

        // Also run subproject tests
        dependsOn(provider {
            subprojects.mapNotNull { it.tasks.findByName("test") }
        })
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}
