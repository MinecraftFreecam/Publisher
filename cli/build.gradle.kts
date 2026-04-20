plugins {
    alias(libs.plugins.kotlin.jvm)
}

version = property("version").toString()
group = property("group").toString()

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.clikt)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
    testImplementation(libs.kotlin.test)
    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":core")))
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
