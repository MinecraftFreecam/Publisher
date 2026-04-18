package net.xolt.freecam.publish

import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.model.CurseForgeConfig
import java.nio.file.Path

fun interface Publisher {
    suspend fun publish(metadata: ReleaseMetadata)
    suspend operator fun invoke(metadata: ReleaseMetadata) = publish(metadata)
}

fun interface PublisherFactory {
    fun create(
        dryRun: Boolean,
        artifactsDir: Path,
        curseforgeConfig: CurseForgeConfig,
    ): Publisher

    operator fun invoke(
        dryRun: Boolean,
        artifactsDir: Path,
        curseforgeConfig: CurseForgeConfig,
    ) = create(dryRun, artifactsDir, curseforgeConfig)
}
