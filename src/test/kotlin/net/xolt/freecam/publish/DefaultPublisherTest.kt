package net.xolt.freecam.publish

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.string.shouldContainOnlyOnce
import io.mockk.MockKMatcherScope
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.publish.model.ReleaseArtifact
import net.xolt.freecam.publish.platforms.CurseForgePlatform
import net.xolt.freecam.publish.platforms.ModrinthPlatform
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.MetadataFixtures.testProjectMetadata
import net.xolt.freecam.test.createTestDir
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.pathString
import kotlin.test.Test

class DefaultPublisherTest {

    private val testVersions = listOf("a", "b", "c", "d").map {
        testProjectMetadata(filename = it)
    }

    @Test
    fun `publisher calls each platform with all artifacts`() = runTest {
        val curseforge = mockk<CurseForgePlatform>(relaxUnitFun = true)
        val modrinth = mockk<ModrinthPlatform>(relaxUnitFun = true)

        val dir = createTestDir()
        val metadata = testMetadata(versions = testVersions)
        val metadataArtifacts = metadata.versions.map {
            dir.resolve(it.filename).apply(Path::createFile)
        }

        DefaultPublisher(dir, curseforge, modrinth).publish(metadata)

        fun MockKMatcherScope.verifyArtifacts() = match<List<ReleaseArtifact>> { artifacts ->
            artifacts.map { it.artifact } == metadataArtifacts
        }

        coVerify { curseforge.publishRelease(metadata, verifyArtifacts()) }
        coVerify { modrinth.publishRelease(metadata, verifyArtifacts()) }
    }

    @Test
    fun `fails if artifact missing`(): Unit = runTest {
        val curseforge = mockk<CurseForgePlatform>(relaxUnitFun = true)
        val modrinth = mockk<ModrinthPlatform>(relaxUnitFun = true)

        val dir = createTestDir()
        val metadata = testMetadata(versions = testVersions)
        val publisher = DefaultPublisher(dir, curseforge, modrinth)

        val ex = shouldThrowExactly<IllegalArgumentException> {
            publisher.publish(metadata)
        }

        ex.message shouldContainOnlyOnce "artifacts were not found"
        assertSoftly {
            metadata.versions
                .map { dir.resolve(it.filename) }
                .forEach { ex.message shouldContainOnlyOnce  it.pathString }
        }
    }
}
