package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.ulong
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.model.CurseForgeConfig

internal class CurseForgeOptionGroup(
    private val metadata: () -> ReleaseMetadata,
) : CurseForgeConfig, OptionGroup(
    name = "CurseForge options",
    help = "Options for configuring CurseForge release publishing",
) {
    override val projectId by option("--curseforge-project-id", envvar = "CURSEFORGE_PROJECT_ID")
        .ulong()
        .help("CurseForge Project ID")
        .defaultLazy(defaultForHelp = "Read from --metadata") {
            metadata().platforms.curseforge.id
        }

    override val token by option("--curseforge-token", envvar = "CURSEFORGE_TOKEN")
        .help("CurseForge API token")
        .required()

    override fun toString(): String {
        val maskedToken = token.take(4).padEnd(10, '*')
        return "CurseForgeOptionGroup(token='$maskedToken', projectId='$projectId')"
    }
}
