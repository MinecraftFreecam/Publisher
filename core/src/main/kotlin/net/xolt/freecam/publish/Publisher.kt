package net.xolt.freecam.publish

import net.xolt.freecam.model.ReleaseMetadata
import java.nio.file.Path

fun interface Publisher {
    suspend fun publish(metadata: ReleaseMetadata)
    suspend operator fun invoke(metadata: ReleaseMetadata) = publish(metadata)
}

fun interface PublisherFactory {
    fun create(
        dryRun: Boolean,
        artifactsDir: Path,
    ): Publisher

    operator fun invoke(
        dryRun: Boolean,
        artifactsDir: Path,
    ) = create(dryRun, artifactsDir)
}
