package net.xolt.freecam.publish.platforms

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.*
import kotlinx.coroutines.test.runTest
import me.hypherionmc.curseupload.errors.InvalidCurseVersionException
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.logging.TestLogger
import net.xolt.freecam.publish.model.CurseForgeConfig
import net.xolt.freecam.publish.model.ReleaseArtifact
import net.xolt.freecam.test.ReleaseArtifactFixtures.testArtifact
import kotlin.test.Test

class DefaultCurseForgePlatformTest {

    private fun createInvalidCurseVersionException(
        invalidVersions: Collection<String> = emptyList(),
        validVersions: Collection<String> = emptyList(),
    ) = InvalidCurseVersionException.of(invalidVersions, validVersions)

    @Test
    fun `retries upload when all invalid versions are snapshots`() = runTest {
        val artifact = testArtifact()

        val invalidVersions = setOf("1.21-snapshot", "26.1-snapshot")

        val exception = createInvalidCurseVersionException(invalidVersions = invalidVersions)

        val client = mockk<CurseForgeClient> {
            coEvery { uploadFile(any(), excludeVersions = emptySet()) } throws exception
            coEvery { uploadFile(any(), excludeVersions = invalidVersions) } just runs
        }

        val platform = DefaultCurseForgePlatform(
            dryRun = false,
            config = mockk<CurseForgeConfig>(),
            logger = TestLogger(),
            client = client,
        )

        platform.publishRelease(
            metadata = mockk<ReleaseMetadata>(),
            artifacts = listOf(artifact),
        )

        coVerify(exactly = 1) {
            client.uploadFile(artifact, excludeVersions = emptySet())
        }

        coVerify(exactly = 1) {
            client.uploadFile(artifact, excludeVersions = invalidVersions)
        }
    }

    @Test
    fun `does not retry upload when any invalid version is not a snapshot`() = runTest {
        val artifact = testArtifact()

        val invalidVersions = setOf("1.21.10", "26.1-snapshot")

        val exception = createInvalidCurseVersionException(invalidVersions = invalidVersions)

        val client = mockk<CurseForgeClient> {
            coEvery { uploadFile(any(), excludeVersions = emptySet()) } throws exception
        }

        val platform = DefaultCurseForgePlatform(
            dryRun = false,
            config = mockk<CurseForgeConfig>(),
            logger = TestLogger(),
            client = client,
        )

        val ex = shouldThrowExactly<InvalidCurseVersionException> {
            platform.publishRelease(
                metadata = mockk<ReleaseMetadata>(),
                artifacts = listOf(artifact),
            )
        }

        ex shouldBeSameInstanceAs exception

        coVerify(exactly = 1) {
            client.uploadFile(artifact, excludeVersions = emptySet())
        }

        coVerify(exactly = 0) {
            client.uploadFile(artifact, excludeVersions = invalidVersions)
        }
    }

    @Test
    fun `does not retry when retry also fails`() = runTest {
        val artifact = testArtifact()

        val invalidVersions = setOf("1.21.10-snapshot", "26.1-snapshot")
        val exception = createInvalidCurseVersionException(invalidVersions = invalidVersions)

        val client = mockk<CurseForgeClient> {
            coEvery { uploadFile(any<ReleaseArtifact>(), excludeVersions = any<Set<String>>()) } throws exception
        }

        val platform = DefaultCurseForgePlatform(
            dryRun = false,
            config = mockk<CurseForgeConfig>(),
            logger = TestLogger(),
            client = client,
        )

        val ex = shouldThrowExactly<InvalidCurseVersionException> {
            platform.publishRelease(
                metadata = mockk<ReleaseMetadata>(),
                artifacts = listOf(artifact),
            )
        }

        ex shouldBeSameInstanceAs exception

        coVerify(exactly = 1) {
            client.uploadFile(artifact, excludeVersions = emptySet())
        }

        coVerify(exactly = 1) {
            client.uploadFile(artifact, excludeVersions = invalidVersions)
        }
    }
}
