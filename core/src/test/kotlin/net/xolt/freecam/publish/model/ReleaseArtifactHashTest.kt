package net.xolt.freecam.publish.model

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import net.xolt.freecam.test.ReleaseArtifactFixtures
import net.xolt.freecam.test.createTestFile
import kotlin.io.path.writeText
import kotlin.test.Test

class ReleaseArtifactHashTest {

    @Test
    fun `produces expected hash`() {
        assertSoftly(
            sequenceOf(
                Fixture(
                    text = "abcde",
                    sha256 = "36bbe50ed96841d10443bcb670d6554f0a34b761be67ec9c4a8ad2c0c44ca42c",
                    sha512 = "878ae65a92e86cac011a570d4c30a7eaec442b85ce8eca0c2952b5e3cc0628c2e79d889ad4d5c7c626986d452dd86374b6ffaa7cd8b67665bef2289a5c70b0a1",
                ),
                Fixture(
                    text = "1234567896456312aoienwrfpoien",
                    sha256 = "6168083e97020ceb012c50b87d96bd7733fe1fb6c413e6cbf5a1cb1454084ef1",
                    sha512 = "72b023f7b55acc1efa833d4b1ce0858103256b38c740b8f917cbac30d0b06d4dd0ddac1b03b8611bded8277e7c38763d3d032dc70019a80b7972252aa26a69a4",
                ),
            )
        ) { forEach { withClue(it.clue, it::test) } }
    }

    private data class Fixture(
        val text: String,
        val clue: String = text,
        val sha256: String,
        val sha512: String,
    ) {
        fun test() {
            val file = createTestFile().also { it.writeText(text) }
            val artifact = ReleaseArtifactFixtures.testArtifact(artifact = file)

            artifact.sha256.toHexString() shouldBe sha256
            artifact.sha512.toHexString() shouldBe sha512
        }
    }
}
