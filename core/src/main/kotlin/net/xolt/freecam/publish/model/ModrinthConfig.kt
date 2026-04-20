package net.xolt.freecam.publish.model

interface ModrinthConfig {
    val projectId: String
    val userAgentVersion: String
    val token: String
    val staging: Boolean
}
