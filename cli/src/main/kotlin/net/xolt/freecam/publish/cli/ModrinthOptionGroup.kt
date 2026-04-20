package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.model.ModrinthConfig

internal class ModrinthOptionGroup(
    override val userAgentVersion: String,
    private val metadata: () -> ReleaseMetadata,
) : ModrinthConfig, OptionGroup(
    name = "Modrinth options",
    help = "Options for configuring Modrinth release publishing",
) {
    override val projectId: String by option("--modrinth-project-id", envvar = "MODRINTH_PROJECT_ID")
        .help("Modrinth Project ID")
        .defaultLazy(defaultForHelp = "Read from --metadata") {
            metadata().platforms.modrinth.id
        }

    override val token: String by option("--modrinth-token", envvar = "MODRINTH_TOKEN")
        .help("Modrinth API token")
        .required()

    override val staging: Boolean by option("--modrinth-staging").flag()
        .help("Use the Modrinth staging API")

    override fun toString(): String {
        val maskedToken = token.take(4).padEnd(10, '*')
        return "ModrinthOptionGroup(token='$maskedToken', userAgentVersion='$userAgentVersion', staging=$staging)"
    }
}
