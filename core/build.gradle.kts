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
    api(project(":schema"))
    implementation(libs.slf4j.api)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
    testImplementation(testFixtures(project(":schema")))
}

tasks.test {
    useJUnitPlatform()
}
