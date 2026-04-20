package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.toTestFile
import kotlin.io.path.absolutePathString
import kotlin.test.Test

class ModrinthOptionGroupTest {

    @Test
    fun `toString masks token`() = runTest {
        val cmd = testCommand(version = "0.0.0")

        cmd.test(listOf("--modrinth-token", "abcdef123456"))

        cmd.modrinth.toString() shouldBe "ModrinthOptionGroup(token='abcd******', userAgentVersion='0.0.0', staging=false)"
    }

    @Test
    fun `project id defaults to metadata`() = runTest {
        val metadata = testMetadata(
            modrinthId = "abcdefg",
        )

        val cmd = testCommand()

        cmd.test(listOf("--metadata", metadata.toTestFile().absolutePathString()))

        cmd.modrinth.projectId shouldBe "abcdefg"
    }

    @Test
    fun `useragent version inherited from command version`() = runTest {
        val cmd = testCommand(version = "1.2.3")

        cmd.test(listOf())

        cmd.modrinth.userAgentVersion shouldBe "1.2.3"
    }
}
