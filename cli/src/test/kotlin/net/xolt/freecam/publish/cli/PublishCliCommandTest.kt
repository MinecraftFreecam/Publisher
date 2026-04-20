package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.publish.Publisher
import net.xolt.freecam.publish.PublisherFactory
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.createTestDir
import net.xolt.freecam.test.createTestFile
import net.xolt.freecam.test.toTestFile
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString
import kotlin.test.Test

class PublishCliCommandTest {

    @Test
    fun `commandName is publish`() {
        val cmd = testCommand()
        cmd.commandName shouldBe "publish"
    }

    @Test
    fun `--help prints usage and exits 0`() = runTest {
        val cmd = testCommand()
        val result = cmd.test("--help")

        result.statusCode shouldBe 0
        result.output shouldContain "Usage"
        result.output shouldContain "artifacts-dir"
    }

    @Test
    fun `--version prints version and exits 0`() = runTest {
        val version = "1.2.3"
        val cmd = testCommand(version = version)
        val result = cmd.test("--version")

        result.output shouldContain "version $version"
        result.statusCode shouldBe 0
    }

    @Test
    fun `dry-run uses dry publisher`() = runTest {
        val metadata = testMetadata()
        val dir = createTestDir()
        var dryPublisher: Boolean? = null
        var actualDir: Path? = null

        val publisher = mockk<Publisher>(relaxUnitFun = true)
        val publisherFactory = PublisherFactory { dryRun, artifactsDir, _, _ ->
            dryPublisher = dryRun
            actualDir = artifactsDir
            publisher
        }

        val cmd = testCommand(publisherFactory = publisherFactory)
        val result = cmd.test(listOf(
            "--dry-run",
            "--curseforge-token", "token",
            "--modrinth-token", "token",
            "--metadata",
            metadata.toTestFile().absolutePathString(),
            dir.absolutePathString(),
        ))

        result.statusCode shouldBe 0
        cmd.dryRun shouldBe true
        dryPublisher shouldBe true
        actualDir shouldBe dir.absolute()
        result.statusCode shouldBe 0
        coVerifySequence { publisher(metadata) }
        confirmVerified(publisher)
    }

    @Test
    fun `non-dry-run uses 'real' publisher`() = runTest {
        val metadata = testMetadata()
        var dryPublisher: Boolean? = null
        var actualDir: Path? = null

        val publisher = mockk<Publisher>(relaxUnitFun = true)
        val publisherFactory = PublisherFactory { dryRun, artifactsDir, _, _ ->
            dryPublisher = dryRun
            actualDir = artifactsDir
            publisher
        }

        // We need a real directory or validation will fail
        val dir = createTestDir()

        val cmd = testCommand(publisherFactory = publisherFactory)
        val result = cmd.test(listOf(
            "--curseforge-token", "token",
            "--modrinth-token", "token",
            "--metadata",
            metadata.toTestFile().absolutePathString(),
            dir.absolutePathString(),
        ))

        result.statusCode shouldBe 0
        cmd.dryRun shouldBe false
        dryPublisher shouldBe false
        actualDir shouldBe dir.absolute()
        coVerifySequence { publisher(metadata) }
        confirmVerified(publisher)
    }

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

internal fun testCommand(
    version: String = "0.0.1",
    publisherFactory: PublisherFactory = mockPublisherFactory(),
) = PublishCliCommand(
    version = version,
    publisherFactory = publisherFactory,
)

internal fun mockPublisherFactory(
    relaxUnitFun: Boolean = false,
) = PublisherFactory { _, _, _, _ ->
    mockk(relaxUnitFun = relaxUnitFun)
}
