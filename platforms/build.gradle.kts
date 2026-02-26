plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

version = property("version").toString()
group = property("group").toString()

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentnegotiation)
    implementation(libs.ktor.client.graphql)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.json)
    testImplementation(libs.kotlin.test)
    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":core")))
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.ktor.client.mock)
}

tasks.test {
    useJUnitPlatform()
}
