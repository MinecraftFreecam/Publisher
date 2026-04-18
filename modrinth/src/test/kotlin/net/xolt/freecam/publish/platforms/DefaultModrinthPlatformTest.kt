package net.xolt.freecam.publish.platforms

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import masecla.modrinth4j.model.version.ProjectVersion
import net.xolt.freecam.publish.logging.TestLogger
import net.xolt.freecam.test.ReleaseArtifactFixtures.testArtifact
import net.xolt.freecam.test.createTestFile
import kotlin.io.path.writeText
import kotlin.test.Test

class DefaultModrinthPlatformTest {

    @Test
    fun `matches artifacts by hash`() = runTest {
        val file = createTestFile().also { it.writeText("abc") }
        val artifact = testArtifact(artifact = file)
        val otherArtifact = testArtifact(artifact = createTestFile())
        val sha512 = artifact.sha512

        val release = mockk<ProjectVersion> {
            every { id } returns "id"
        }

        val client = mockk<ModrinthClient> {
            coEvery { getVersionsByHash(any()) } returns mapOf(
                sha512 to release,
            )
        }

        val platform = DefaultModrinthPlatform(client, TestLogger())

        val result = with(platform) {
            listOf(otherArtifact, artifact, otherArtifact).alreadyUploaded()
        }

        result shouldContainExactly listOf(artifact to release)
        result.single().first shouldBeSameInstanceAs artifact
    }
}
