package net.xolt.freecam.test

import net.xolt.freecam.model.Environment
import net.xolt.freecam.model.Relationship
import net.xolt.freecam.model.ReleaseType
import net.xolt.freecam.publish.model.ReleaseArtifact
import java.nio.file.Path

object ReleaseArtifactFixtures {
    fun testArtifact(
        displayName: String = "Fake Release",
        version: String = "1.2.3",
        versionType: ReleaseType = ReleaseType.RELEASE,
        changelog: String = "",
        environments: Set<Environment> = emptySet(),
        loaders: Set<String> = setOf("loader"),
        gameVersion: String = "1.2.3",
        gameVersions: Set<String> = emptySet(),
        javaVersions: Set<String> = emptySet(),
        relationships: Set<Relationship> = emptySet(),
        artifact: Path = Path.of(""),
    ) = ReleaseArtifact(
        displayName = displayName,
        version = version,
        versionType = versionType,
        changelog = changelog,
        environments = environments,
        loaders = loaders,
        gameVersion = gameVersion,
        gameVersions = gameVersions,
        javaVersions = javaVersions,
        relationships = relationships,
        artifact = artifact,
    )
}
