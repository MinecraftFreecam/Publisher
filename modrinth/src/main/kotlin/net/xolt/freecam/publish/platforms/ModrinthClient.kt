package net.xolt.freecam.publish.platforms

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import masecla.modrinth4j.endpoints.version.CreateVersion
import masecla.modrinth4j.main.ModrinthAPI
import masecla.modrinth4j.model.version.FileHash
import masecla.modrinth4j.model.version.ProjectVersion
import net.xolt.freecam.publish.model.ModrinthConfig
import net.xolt.freecam.publish.model.ReleaseArtifact

internal class ModrinthClient(
    private val dryRun: Boolean,
    private val projectId: String,
    private val api: ModrinthAPI,
) {

    constructor(
        dryRun: Boolean,
        config: ModrinthConfig,
    ) : this(
        dryRun = dryRun,
        projectId = config.projectId,
        api = config.createModrinthApi(),
    )

    suspend fun getVersionsByHash(sha512s: List<ByteArray>): Map<ByteArray, ProjectVersion> {
        val hashes = sha512s.map { it.toHexString() }
        val result =
            if (dryRun) emptyMap()
            else withContext(Dispatchers.IO) {
                api.versions().files().getVersionByHash(FileHash.SHA512, hashes).join()
            } ?: error("Failed to retrieve versions for hashes: $hashes")
        return result.mapKeys { it.key.hexToByteArray() }
    }

    suspend fun uploadVersion(spec: ReleaseArtifact) = uploadVersion {
        projectId(projectId)
        name(spec.displayName)
        changelog(spec.changelog)
        versionNumber(spec.version)
        versionType(spec.versionType.toModrinth())
        loaders(spec.loaders.toList())
        files(spec.artifact.toFile())

        spec.modrinthGameVersions?.let {
            gameVersions(it)
        }

        spec.modrinthRelationships?.let {
            dependencies(it)
        }
    }

    private suspend fun uploadVersion(request: CreateVersion.CreateVersionRequest.CreateVersionRequestBuilder.() -> Unit) {
        val request = CreateVersion.CreateVersionRequest.builder().apply(request).build()
        if (dryRun) {
            // TODO: log
        } else {
            withContext(Dispatchers.IO) { api.versions().createProjectVersion(request).join() }
        }
    }
}
