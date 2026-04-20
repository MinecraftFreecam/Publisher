package net.xolt.freecam.publish.model

import net.xolt.freecam.model.Environment
import net.xolt.freecam.model.Relationship
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.model.ReleaseType
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.inputStream
import kotlin.io.path.name

private const val SHA_256 = "SHA-256"
private const val SHA_512 = "SHA-512"
private const val BUFFER_SIZE = 0x2000

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

    val sha256: ByteArray
        get() = hashes[SHA_256]!!

    val sha512: ByteArray
        get() = hashes[SHA_512]!!

    private val hashes: Map<String, ByteArray> by lazy {
        sequenceOf(
            SHA_256,
            SHA_512,
        ).associateWith {
            MessageDigest.getInstance(it)
        }.also { digestMap ->
            val digests = digestMap.values
            artifact.inputStream().use { input ->
                val buffer = ByteArray(BUFFER_SIZE)
                generateSequence { input.read(buffer) }
                    .takeWhile { it > -1 }
                    .forEach { len ->
                        digests.forEach {
                            it.update(buffer, 0, len)
                        }
                    }
            }
        }.mapValues { (_, digest) ->
            digest.digest()
        }
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
