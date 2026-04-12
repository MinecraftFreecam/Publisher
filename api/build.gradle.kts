plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

version = property("api_version").toString()
group = property("version").toString()

dependencies {
    implementation(libs.kotlin.semver)
    implementation(libs.kotlin.serialization.json)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
}

tasks.test {
    useJUnitPlatform()
}
