package net.xolt.freecam.publish.platforms

import masecla.modrinth4j.model.version.ProjectVersion
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.logging.LogLevel
import net.xolt.freecam.publish.logging.Logger
import net.xolt.freecam.publish.model.ModrinthConfig
import net.xolt.freecam.publish.model.ReleaseArtifact

interface ModrinthPlatform : Platform { companion object }

fun ModrinthPlatform.Companion.create(
    dryRun: Boolean = false,
    config: ModrinthConfig,
): ModrinthPlatform = DefaultModrinthPlatform(
    dryRun = dryRun,
    config = config,
)

private fun createLogger(dryRun: Boolean) = Logger.default
    .let { if (dryRun) it.scoped("dry-run") else it }
    .scoped("Modrinth")

internal class DefaultModrinthPlatform(
    private val client: ModrinthClient,
    private val logger: Logger,
) : ModrinthPlatform {

    constructor(
        dryRun: Boolean,
        config: ModrinthConfig,
        logger: Logger = createLogger(dryRun),
    ) : this(
        client = ModrinthClient(dryRun, config),
        logger = logger,
    )

    override suspend fun publishRelease(
        metadata: ReleaseMetadata,
        artifacts: List<ReleaseArtifact>,
    ) {
        // TODO: also check for "matching version+mcVersion+loader" releases
        runCatching { artifacts.alreadyUploaded() }
            .getOrElse { err ->
                logger.error { "Failed to check for already-uploaded artifacts by hash: $err" }
                null
            }
            .takeUnless { it.isNullOrEmpty() }
            ?.let { uploaded ->
                val all = uploaded.size == artifacts.size
                val level = if (all) LogLevel.ERROR else LogLevel.WARNING
                logger.log(level) {
                    val count = if (all) "All" else "${uploaded.size}/${artifacts.size}"
                    val desc = if (uploaded.size > 1) "artifacts have" else "artifact has"
                    val list = uploaded.joinToString("\n") { (spec, release) ->
                        "- ${spec.name} (ID '${release.id}', published ${release.datePublished})"
                    }
                    "$count $desc already been uploaded with matching hash:\n$list"
                }
            }

        artifacts.forEach { spec ->
            logger.info { "publishing artifact ${spec.name}" }
            client.uploadVersion(spec)
        }
    }

    /**
     * Resolve already released [ProjectVersion]s that have the same file by SHA-512 hash lookup.
     */
    internal suspend fun List<ReleaseArtifact>.alreadyUploaded(): List<Pair<ReleaseArtifact, ProjectVersion>> =
        client.getVersionsByHash(map { it.sha512 })
            .asSequence()
            .mapNotNull { (hash, release) ->
                find { it.sha512 contentEquals hash }
                    ?.let { it to release }
                    ?: run {
                        logger.error { "Unable to reconcile ${hash.toHexString()} with any artifact" }
                        null
                    }
            }
            .toList()
}
