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
                ),
                Fixture(
                    text = "1234567896456312aoienwrfpoien",
                    sha256 = "6168083e97020ceb012c50b87d96bd7733fe1fb6c413e6cbf5a1cb1454084ef1",
                ),
            )
        ) { forEach { withClue(it.clue, it::test) } }
    }

    private data class Fixture(
        val text: String,
        val clue: String = text,
        val sha256: String,
    ) {
        fun test() {
            val file = createTestFile().also { it.writeText(text) }
            val artifact = ReleaseArtifactFixtures.testArtifact(artifact = file)

            artifact.sha256.toHexString() shouldBe sha256
        }
    }
}
