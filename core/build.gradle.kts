plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-test-fixtures`
}

version = property("version").toString()
group = property("group").toString()

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(project(":api"))
    implementation(libs.slf4j.api)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
    testImplementation(testFixtures(project(":api")))
}

tasks.test {
    useJUnitPlatform()
}
