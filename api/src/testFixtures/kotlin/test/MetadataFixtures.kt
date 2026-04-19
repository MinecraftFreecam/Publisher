package net.xolt.freecam.test

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import net.xolt.freecam.model.*
import java.nio.file.Path
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

    fun testProjectMetadata(
        displayName: String = "Fake Project",
        environments: List<Environment> = listOf(Environment.CLIENT),
        loader: String = "fabric",
        minecraft: String = "26.1",
        filename: String = "a.jar",
        gameVersions: List<String> = emptyList(),
        javaVersions: List<String> = emptyList(),
        relationships: List<Relationship> = emptyList(),
    ) = ProjectReleaseMetadata(
        displayName = displayName,
        environments = environments,
        loader = loader,
        minecraft = minecraft,
        filename = filename,
        gameVersions = gameVersions,
        javaVersions = javaVersions,
        relationships = relationships,
    )
}

@OptIn(ExperimentalSerializationApi::class)
fun ReleaseMetadata.toTestFile(): Path =
    createTestFile().also {
        it.outputStream().use { out ->
            Json.encodeToStream(this, out)
        }
    }
