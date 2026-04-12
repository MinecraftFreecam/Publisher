plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-test-fixtures`
}

version = property("api_version").toString()
group = property("version").toString()

dependencies {
    implementation(libs.kotlin.semver)
    implementation(libs.kotlin.serialization.json)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
    testFixturesImplementation(libs.kotlin.serialization.json)
}

tasks.test {
    useJUnitPlatform()
}
