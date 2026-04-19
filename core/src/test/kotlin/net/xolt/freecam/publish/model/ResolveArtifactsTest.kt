package net.xolt.freecam.publish.model

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import net.xolt.freecam.model.ReleaseType
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.MetadataFixtures.testProjectMetadata
import net.xolt.freecam.test.createTestDir
import kotlin.io.path.Path
import kotlin.test.Test

class ResolveArtifactsTest {

    @Test
    fun `resolves to expected artifacts`() {
        val artifactsDir = createTestDir()
        val filenames = listOf("a", "b", "c")
        val metadata = testMetadata(
            versions = filenames.map { testProjectMetadata(filename = it) },
        )

        val artifacts: List<ReleaseArtifact> = metadata.resolveArtifacts(artifactsDir)

        artifacts.map { it.name } shouldContainExactly filenames
        artifacts.map { it.artifact } shouldContainExactly filenames.map {
            artifactsDir.resolve(it)
        }
    }

    @Test
    fun `retains top-level metadata`() {
        val artifactsDir = Path("")
        val metadata = testMetadata(
            modVersion = "1.2.3",
            releaseType = ReleaseType.ALPHA,
            changelog = "Changelog",
            versions = List(10) { testProjectMetadata() },
        )

        val artifacts: List<ReleaseArtifact> = metadata.resolveArtifacts(artifactsDir)

        assertSoftly(artifacts) {
            size shouldBe 10
            forEach {
                it.version shouldBe "1.2.3"
                it.versionType shouldBe ReleaseType.ALPHA
                it.changelog shouldBe "Changelog"
            }
        }
    }
}
