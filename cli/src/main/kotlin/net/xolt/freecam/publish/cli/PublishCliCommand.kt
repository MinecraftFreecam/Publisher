package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.PublisherFactory
import net.xolt.freecam.publish.logging.*
import net.xolt.freecam.publish.model.CurseForgeConfig
import java.nio.file.Path
import kotlin.io.path.inputStream

internal class PublishCliCommand(
    version: String = "0.0.1",
    publisherFactory: PublisherFactory,
) : SuspendingCliktCommand(name = "publish") {

    init {
        context {
            versionOption(version)
            helpFormatter = {
                MordantHelpFormatter(
                    context = it,
                    requiredOptionMarker = null,
                    showDefaultValues = true,
                    showRequiredTag = true,
                )
            }
        }
    }

    private val publisher by lazy {
        publisherFactory(
            dryRun = dryRun,
            artifactsDir = artifactsDir,
            curseforgeConfig = curseforge,
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    val metadata: ReleaseMetadata by lazy {
        metadataPath.inputStream().use { input ->
            Json.decodeFromStream<ReleaseMetadata>(input)
        }
    }

    val artifactsDir: Path by argument("artifacts-dir")
        .path(mustExist = true, mustBeReadable = true, canBeFile = false, canBeDir = true)
        .help("Directory containing the release artifacts")

    val metadataPath: Path by option("--metadata")
        .path(mustExist = true, mustBeReadable = true, canBeFile = true, canBeDir = false)
        .help("Release metadata JSON file")
        .defaultLazy(defaultForHelp = "[artifacts-dir]/release-metadata.json") {
            artifactsDir.resolve("release-metadata.json")
        }

    val dryRun: Boolean by option("--dry-run").flag()
        .help("Perform a dry run without making any actual API calls")

    val curseforge: CurseForgeConfig by CurseForgeOptionGroup { metadata }

    private val verbosity by VerbosityOptionGroup()
    val logLevel: LogLevel get() = verbosity.level

    val ghaAnnotations by option("--gha-output", envvar = "GITHUB_ACTIONS")
        .help("Format output using GitHub Actions annotations (::error::, ::warning::, etc)")
        .flag()

    override suspend fun run() {
        Logger.configure {
            threshold = logLevel
            renderer =
                if (ghaAnnotations) GHALogRenderer
                else MordantLogRenderer
            output = { message ->
                // Use Clikt's `echo` for system-specific line endings and Mordant rendering.
                echo(message, err = level.isStderr)
            }
        }

        publisher(metadata)
    }
}
