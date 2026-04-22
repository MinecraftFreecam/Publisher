package net.xolt.freecam.publish.platforms

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.hypherionmc.curseupload.CurseUploadApi
import me.hypherionmc.curseupload.constants.CurseChangelogType
import me.hypherionmc.curseupload.constants.GameType
import me.hypherionmc.curseupload.requests.CurseArtifact
import net.xolt.freecam.model.Relationship
import net.xolt.freecam.publish.logging.Logger
import net.xolt.freecam.publish.model.CurseForgeConfig
import net.xolt.freecam.publish.model.ReleaseArtifact
import java.io.File

internal class CurseForgeClient(
    private val projectId: Long,
    private val api: CurseUploadApi,
    private val logger: Logger,
) {

    constructor(
        dryRun: Boolean,
        config: CurseForgeConfig,
        logger: Logger,
    ) : this(
        projectId = config.projectId.toLong(),
        api = CurseUploadApi(config.token, logger::curseApiMessage).apply {
            // Instructs api.upload() to print instead of upload
            isDebug = dryRun
            gameType = GameType.MINECRAFT
        },
        logger,
    )

     suspend fun uploadFile(spec: ReleaseArtifact) = uploadFile(spec.artifact.toFile()) {
         displayName(spec.displayName)
         releaseType(spec.versionType.toCurseForge())

         spec.changelog.takeIf { it.isNotBlank() }?.let {
             changelog(it)
             changelogType(CurseChangelogType.MARKDOWN)
         }

         sequenceOf(
             spec.loaders,
             spec.curseForgeEnvironments,
             spec.curseForgeJavaVersions,
             spec.curseForgeMinecraftVersions,
         ).flatten().forEach(::addGameVersion)

         spec.relationships.forEach { (slug, _, type) ->
             when (type) {
                 Relationship.Type.REQUIRED -> requirement(slug)
                 Relationship.Type.OPTIONAL -> optional(slug)
                 Relationship.Type.BUNDLED -> embedded(slug)
             }
         }
     }

    suspend fun uploadFile(file: File, builder: CurseArtifact.() -> Unit) {
        val artifact = CurseArtifact(file, projectId).apply(builder)
        withContext(Dispatchers.IO) { api.upload(artifact) }
    }
}
