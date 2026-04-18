package net.xolt.freecam.publish.platforms

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import masecla.modrinth4j.model.version.ProjectVersion.ProjectDependencyType
import net.xolt.freecam.model.Relationship
import net.xolt.freecam.test.ReleaseArtifactFixtures.testArtifact
import kotlin.test.Test

class ModrinthExtensionsTest {

    @Test
    fun `lowercases and filters snapshots`() {
        val artifact = testArtifact(gameVersions = setOf(
            "1.20.1",
            "1.21-SNAPSHOT",
        ))

        artifact.modrinthGameVersions shouldContainExactly listOf(
            "1.20.1",
        )
    }

    @Test
    fun `returns null when no valid versions`() {
        val artifact = testArtifact(gameVersions = setOf(
            "1.21-SNAPSHOT",
        ))

        artifact.modrinthGameVersions.shouldBeNull()
    }

    @Test
    fun `maps relationships`() {
        val artifact = testArtifact(
            relationships = setOf(
                Relationship(curseforgeSlug = "slug1", modrinthId = "id1", type = Relationship.Type.REQUIRED),
                Relationship(curseforgeSlug = "slug2", modrinthId = "id2", type = Relationship.Type.OPTIONAL),
            )
        )

        val deps = artifact.modrinthRelationships!!

        deps.map { it.projectId to it.dependencyType } shouldContainExactly listOf(
            "id1" to ProjectDependencyType.REQUIRED,
            "id2" to ProjectDependencyType.OPTIONAL,
        )
    }
}
