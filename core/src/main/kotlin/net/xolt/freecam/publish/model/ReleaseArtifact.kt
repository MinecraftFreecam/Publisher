package net.xolt.freecam.publish.model

import net.xolt.freecam.model.ProjectReleaseMetadata
import net.xolt.freecam.model.Relationship
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.inputStream
import kotlin.io.path.name

data class ReleaseArtifact(
    val displayName: String,
    val loader: String,
    val minecraftVersion: String,
    val gameVersions: Set<String>,
    val javaVersions: Set<String>,
    val relationships: Set<Relationship>,
    val artifact: Path,
) {
    val name get() = artifact.name

    val size: Long by lazy {
        artifact.toFile().length()
    }

    val sha256: ByteArray by lazy {
        val digest = MessageDigest.getInstance("SHA-256")
        artifact.inputStream().use { input ->
            val buffer = ByteArray(0x2000)
            generateSequence { input.read(buffer) }
                .takeWhile { it > -1 }
                .forEach { len ->
                    digest.update(buffer, 0, len)
                }
        }
        digest.digest()
    }

    companion object
}

fun ReleaseArtifact.Companion.from(
    metadata: ProjectReleaseMetadata,
    artifactSupplier: (String) -> Path,
) = ReleaseArtifact(
    displayName = metadata.displayName,
    loader = metadata.loader,
    minecraftVersion = metadata.minecraft,
    gameVersions = metadata.gameVersions.toSet(),
    javaVersions = metadata.javaVersions.toSet(),
    relationships = metadata.relationships.toSet(),
    artifact = artifactSupplier(metadata.filename),
)

fun Path.resolveArtifact(metadata: ProjectReleaseMetadata) =
    ReleaseArtifact.from(metadata, ::resolve)
