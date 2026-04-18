package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.createTestFile
import net.xolt.freecam.test.toTestFile
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString
import kotlin.test.Test

class PublishCliCommandValidationTest {

    @Test
    fun `missing artifacts-dir fails validation`() = runTest {
        val metadata = testMetadata()
        val dir = Path("artifacts-dir")
        val cmd = testCommand()

        val result = cmd.test(listOf(
            "--metadata",
            metadata.toTestFile().absolutePathString(),
            dir.absolutePathString(),
        ))

        result.statusCode shouldBe 1
        result.stderr shouldContain "\"${dir.absolute()}\" does not exist"
    }

    @Test
    fun `non-dir artifacts-dir fails validation`() = runTest {
        val metadata = testMetadata()

        val file = createTestFile()
        val cmd = testCommand()

        val result = cmd.test(listOf(
            "--metadata",
            metadata.toTestFile().absolutePathString(),
            file.absolutePathString(),
        ))

        result.statusCode shouldBe 1
        result.stderr shouldContain "\"${file.absolute()}\" is a file"
    }
}
