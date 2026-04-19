package net.xolt.freecam.publish

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.string.shouldContainOnlyOnce
import kotlinx.coroutines.test.runTest
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
        val dir = createTestDir()
        val metadata = testMetadata(versions = testVersions)
        val metadataArtifacts = metadata.versions.map {
            dir.resolve(it.filename).apply(Path::createFile)
        }

        DefaultPublisher(dir).publish(metadata)

        // TODO: verify
    }

    @Test
    fun `fails if artifact missing`(): Unit = runTest {
        val dir = createTestDir()
        val metadata = testMetadata(versions = testVersions)
        val publisher = DefaultPublisher(dir)

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
