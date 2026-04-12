package net.xolt.freecam.test

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import net.xolt.freecam.model.Platforms
import net.xolt.freecam.model.ProjectReleaseMetadata
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.model.ReleaseType
import java.io.File
import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.outputStream

object MetadataFixtures {
    fun testMetadata(
        modVersion: String = "0.1.0",
        releaseType: ReleaseType = ReleaseType.RELEASE,
        displayName: String = "Fake Release",
        changelog: String = "Changelog",
        githubTag: String = "v0.1.0",
        modrinthId: String = "id",
        curseforgeId: String = "id",
        versions: List<ProjectReleaseMetadata> = emptyList(),
    ) = ReleaseMetadata(
        modVersion = modVersion,
        releaseType = releaseType,
        displayName = displayName,
        changelog = changelog,
        platforms = Platforms(
            github = Platforms.Github(githubTag),
            modrinth = Platforms.Modrinth(modrinthId),
            curseforge = Platforms.Curseforge(curseforgeId),
        ),
        versions = versions,
    )
}

@OptIn(ExperimentalSerializationApi::class)
fun ReleaseMetadata.toTestFile(): Path =
    createTestFile().also {
        it.outputStream().use { out ->
            Json.encodeToStream(this, out)
        }
    }
