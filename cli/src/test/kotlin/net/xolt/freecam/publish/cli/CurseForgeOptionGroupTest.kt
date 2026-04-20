package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.toTestFile
import kotlin.io.path.absolutePathString
import kotlin.test.Test

class CurseForgeOptionGroupTest {

    @Test
    fun `toString masks token`() = runTest {
        val cmd = testCommand()

        cmd.test(listOf("--curseforge-token", "abcdef123456", "--curseforge-project-id", "12345"))

        cmd.curseforge.toString() shouldBe "CurseForgeOptionGroup(token='abcd******', projectId='12345')"
    }

    @Test
    fun `project id defaults to metadata`() = runTest {
        val metadata = testMetadata(
            curseforgeId = 123456789UL,
        )

        val cmd = testCommand()

        cmd.test(listOf("--metadata", metadata.toTestFile().absolutePathString()))

        cmd.curseforge.projectId shouldBe 123456789UL
    }
}
