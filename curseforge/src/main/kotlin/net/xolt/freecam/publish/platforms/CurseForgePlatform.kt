package net.xolt.freecam.publish.platforms

import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.logging.Logger
import net.xolt.freecam.publish.model.CurseForgeConfig
import net.xolt.freecam.publish.model.ReleaseArtifact

interface CurseForgePlatform : Platform { companion object }

fun CurseForgePlatform.Companion.create(
    dryRun: Boolean = false,
    config: CurseForgeConfig,
): CurseForgePlatform = DefaultCurseForgePlatform(
    dryRun = dryRun,
    config = config,
)

private fun createLogger(dryRun: Boolean) = Logger.default
    .let { if (dryRun) it.scoped("dry-run") else it }
    .scoped("CurseForge")

internal class DefaultCurseForgePlatform(
    dryRun: Boolean,
    private val config: CurseForgeConfig,
    private val logger: Logger = createLogger(dryRun),
    private val client: CurseForgeClient = CurseForgeClient(
        dryRun = dryRun,
        config = config,
        logger = logger,
    ),
) : CurseForgePlatform {

    override suspend fun publishRelease(metadata: ReleaseMetadata, artifacts: List<ReleaseArtifact>) {
        artifacts.forEach { spec ->
            logger.info { "publishing artifact ${spec.name}" }
            client.uploadFile(spec)
        }
    }
}
