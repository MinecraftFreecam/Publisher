package net.xolt.freecam.publish

import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.model.ReleaseArtifact
import net.xolt.freecam.publish.model.resolveArtifacts
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

val DefaultPublisherFactory = PublisherFactory { dryRun, artifactsDir ->
    DefaultPublisher(
        artifactsDir = artifactsDir,
    )
}

data class DefaultPublisher(
    val artifactsDir: Path,
) : Publisher {

    override suspend fun publish(metadata: ReleaseMetadata) {
        val artifacts = metadata.resolveArtifacts(artifactsDir).apply {
            verifyExists()
        }
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
