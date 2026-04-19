package net.xolt.freecam.publish.model

import net.xolt.freecam.model.Environment
import net.xolt.freecam.model.Relationship
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.model.ReleaseType
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.inputStream
import kotlin.io.path.name

data class ReleaseArtifact(
    val displayName: String,
    val version: String,
    val versionType: ReleaseType,
    val changelog: String,
    val environments: Set<Environment>,
    val loaders: Set<String>,
    val gameVersion: String,
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
}

fun ReleaseMetadata.resolveArtifacts(artifactsDir: Path): List<ReleaseArtifact>
    = versions
    .asSequence()
    .sorted()
    .map {
        ReleaseArtifact(
            displayName = it.displayName,
            version = modVersion,
            versionType = releaseType,
            changelog = changelog,
            environments = it.environments.toSet(),
            loaders = setOf(it.loader),
            gameVersion = it.minecraft,
            gameVersions = it.gameVersions.toSet(),
            javaVersions = it.javaVersions.toSet(),
            relationships = it.relationships.toSet(),
            artifact = artifactsDir.resolve(it.filename),
        )
    }
    .toList()
