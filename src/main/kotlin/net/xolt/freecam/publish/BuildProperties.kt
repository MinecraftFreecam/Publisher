package net.xolt.freecam.publish

import java.util.Properties

object BuildProperties {

    val version: String
        get() = properties["version"] as String

    val apiVersion: String
        get() = properties["api_version"] as String

    private val properties: Properties by lazy {
        requireNotNull(javaClass.getResourceAsStream("/build.properties")) {
            "build.properties not found on classpath"
        }.use { input ->
            Properties().apply { load(input) }
        }
    }
}
