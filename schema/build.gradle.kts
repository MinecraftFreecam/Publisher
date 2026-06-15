plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-test-fixtures`
}

version = property("schema_version").toString()
group = property("version").toString()

kotlin {
    jvmToolchain(21)
}

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
