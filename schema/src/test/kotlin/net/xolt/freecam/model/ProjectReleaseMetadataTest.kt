package net.xolt.freecam.model

import io.kotest.matchers.collections.shouldContainExactly
import net.xolt.freecam.test.MetadataFixtures.testProjectMetadata
import kotlin.test.Test

class ProjectReleaseMetadataVersionTest {

    @Test
    fun `sorts by minecraft version`() {
        val items = setOf(
            "1.8.9",
            "1.20",
            "1.21.11",
            "1.21.11-pre1",
            "1.21.11-pre5",
            "1.21.11-rc1",
            "26.1",
            "26.1-snapshot-11",
            "26.1.1",
            "26.1.1-snapshot-3",
        ).map { testProjectMetadata(minecraft = it) }

        val sorted = items.sorted()

        sorted.map { it.minecraft } shouldContainExactly listOf(
            "1.8.9",
            "1.20",
            "1.21.11-pre1",
            "1.21.11-pre5",
            "1.21.11-rc1",
            "1.21.11",
            "26.1-snapshot-11",
            "26.1",
            "26.1.1-snapshot-3",
            "26.1.1",
        )
    }

    @Test
    fun `loader is used as secondary sort key`() {
        val items = listOf(
            testProjectMetadata(minecraft = "1.21.11", loader = "forge"),
            testProjectMetadata(minecraft = "1.21.11", loader = "fabric"),
        )

        val sorted = items.sorted()

        sorted.map { it.loader } shouldContainExactly listOf("fabric", "forge")
    }

    @Test
    fun `filename is used as tertiary sort key`() {
        val items = listOf(
            testProjectMetadata(minecraft = "1.21.11", loader = "fabric", filename = "b.jar"),
            testProjectMetadata(minecraft = "1.21.11", loader = "fabric", filename = "a.jar"),
        )

        val sorted = items.sorted()

        sorted.map { it.filename } shouldContainExactly listOf("a.jar", "b.jar")
    }

    @Test
    fun `sorts by version then loader then filename together`() {
        val items = listOf(
            testProjectMetadata(minecraft = "1.21.11", loader = "forge", filename = "b.jar"),
            testProjectMetadata(minecraft = "1.21.11", loader = "fabric", filename = "b.jar"),
            testProjectMetadata(minecraft = "1.21.11", loader = "fabric", filename = "a.jar"),
        )

        val sorted = items.sorted()

        sorted.map { it.loader to it.filename } shouldContainExactly listOf(
            "fabric" to "a.jar",
            "fabric" to "b.jar",
            "forge" to "b.jar",
        )
    }
}
