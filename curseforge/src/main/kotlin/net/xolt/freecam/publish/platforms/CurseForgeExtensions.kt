package net.xolt.freecam.publish.platforms

import me.hypherionmc.curseupload.constants.CurseReleaseType
import net.xolt.freecam.model.ReleaseType
import net.xolt.freecam.publish.model.ReleaseArtifact

private val JAVA_VERSION_PATTERN = "^java_(\\d+)$".toRegex()
private val LEGACY_SNAPSHOT_PATTERN = "^[A-Za-z0-9]+$".toRegex()
private val PRE_RELEASE_PATTERN = "^(.*)-(pre|rc)-\\d+$".toRegex()
private val SNAPSHOT_PATTERN = "^(.*)-snapshot-\\d+$".toRegex()

internal fun ReleaseType.toCurseForge(): CurseReleaseType = when (this) {
    ReleaseType.RELEASE -> CurseReleaseType.RELEASE
    ReleaseType.RELEASE_CANDIDATE -> CurseReleaseType.BETA
    ReleaseType.BETA -> CurseReleaseType.BETA
    ReleaseType.ALPHA -> CurseReleaseType.ALPHA
}

internal val ReleaseArtifact.curseForgeEnvironments: List<String>
    get() = environments.map { it.toString().lowercase() }

internal val ReleaseArtifact.curseForgeJavaVersions: List<String>
    get() = javaVersions.map {
        val match = requireNotNull(JAVA_VERSION_PATTERN.find(it)) {
            "Invalid Java Version: $it"
        }
        val version = match.groupValues[1].toUInt()
        "Java $version"
    }

internal val ReleaseArtifact.curseForgeMinecraftVersions: List<String>
    get() = gameVersions
        .filter { version ->
            sequenceOf(
                LEGACY_SNAPSHOT_PATTERN,
                PRE_RELEASE_PATTERN,
            ).none(version::matches)
        }
        .map { version ->
            SNAPSHOT_PATTERN.find(version)
                ?.let { "${it.groupValues[1]}-snapshot" }
                ?: version
        }
