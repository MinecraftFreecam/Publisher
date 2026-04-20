package net.xolt.freecam.publish.platforms

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldContainExactly
import net.xolt.freecam.model.Environment
import net.xolt.freecam.test.ReleaseArtifactFixtures.testArtifact
import kotlin.test.Test

class CurseForgeExtensionsTest {

    @Test
    fun `maps valid java versions`() {
        val artifact = testArtifact(javaVersions = setOf("java_8", "java_17"))

        artifact.curseForgeJavaVersions shouldContainExactly listOf(
            "Java 8",
            "Java 17",
        )
    }

    @Test
    fun `fails on invalid java version`() {
        val artifact = testArtifact(javaVersions = setOf("java8"))

        shouldThrowExactly<IllegalArgumentException> {
            artifact.curseForgeJavaVersions
        }
    }

    @Test
    fun `filters legacy and pre-release versions`() {
        val artifact = testArtifact(gameVersions = setOf(
            "1.20.1",
            "1.21-pre-1",
            "23w13a",           // legacy snapshot
        ))

        artifact.curseForgeMinecraftVersions shouldContainExactly listOf(
            "1.20.1",
        )
    }

    @Test
    fun `normalizes snapshot versions`() {
        val artifact = testArtifact(gameVersions = setOf(
            "1.21-snapshot-5",
        ))

        artifact.curseForgeMinecraftVersions shouldContainExactly listOf(
            "1.21-snapshot",
        )
    }

    @Test
    fun `maps environments to lowercase`() {
        val artifact = testArtifact(environments = setOf(Environment.CLIENT, Environment.SERVER))

        artifact.curseForgeEnvironments shouldContainExactly listOf("client", "server")
    }
}
