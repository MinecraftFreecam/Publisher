package net.xolt.freecam.publish

import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.model.ReleaseArtifact
import net.xolt.freecam.publish.model.resolveArtifacts
import net.xolt.freecam.publish.platforms.CurseForgePlatform
import net.xolt.freecam.publish.platforms.create
import net.xolt.freecam.publish.platforms.ModrinthPlatform
import net.xolt.freecam.publish.platforms.create
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

val DefaultPublisherFactory = PublisherFactory { dryRun, artifactsDir, curseforgeConfig, modrinthConfig ->
    DefaultPublisher(
        artifactsDir = artifactsDir,
        curseforge = CurseForgePlatform.create(dryRun, curseforgeConfig),
        modrinth = ModrinthPlatform.create(dryRun, modrinthConfig),
    )
}

data class DefaultPublisher(
    val artifactsDir: Path,
    val curseforge: CurseForgePlatform,
    val modrinth: ModrinthPlatform,
) : Publisher {

    override suspend fun publish(metadata: ReleaseMetadata) {
        val artifacts = metadata.resolveArtifacts(artifactsDir).apply {
            verifyExists()
        }
        curseforge.publishRelease(metadata, artifacts)
        modrinth.publishRelease(metadata, artifacts)
    }

    private fun List<ReleaseArtifact>.verifyExists() {
        filterNot { it.artifact.exists() }
            .takeUnless { it.isEmpty() }
            ?.joinToString("\n") { "- ${it.artifact.pathString}" }
            ?.let { files ->
                throw IllegalArgumentException("The following artifacts were not found:\n$files")
            }
    }
}
