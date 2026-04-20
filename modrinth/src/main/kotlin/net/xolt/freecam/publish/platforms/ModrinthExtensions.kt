package net.xolt.freecam.publish.platforms

import masecla.modrinth4j.client.agent.UserAgent.builder
import masecla.modrinth4j.main.ModrinthAPI
import masecla.modrinth4j.model.version.ProjectVersion
import masecla.modrinth4j.model.version.ProjectVersion.ProjectDependencyType
import masecla.modrinth4j.model.version.ProjectVersion.VersionType
import net.xolt.freecam.model.Relationship
import net.xolt.freecam.model.ReleaseType
import net.xolt.freecam.publish.model.ModrinthConfig
import net.xolt.freecam.publish.model.ReleaseArtifact

private const val USER = "MinecraftFreecam"
private const val PROJECT = "Publisher"
private const val MODRINTH_API = "https://api.modrinth.com/v2"
private const val MODRINTH_STAGING_API = "https://staging-api.modrinth.com/v2"

internal fun ModrinthConfig.createModrinthApi(): ModrinthAPI {
    val userAgent = builder().apply {
        authorUsername(USER)
        projectName(PROJECT)
        projectVersion(userAgentVersion)
    }.build()

    val url = if (staging) MODRINTH_STAGING_API else MODRINTH_API

    return ModrinthAPI.rateLimited(userAgent, url, token)
}

internal fun ReleaseType.toModrinth(): VersionType = when (this) {
    ReleaseType.RELEASE -> VersionType.RELEASE
    ReleaseType.RELEASE_CANDIDATE -> VersionType.BETA
    ReleaseType.BETA -> VersionType.BETA
    ReleaseType.ALPHA -> VersionType.ALPHA
}

internal fun Relationship.Type.toModrinth(): ProjectDependencyType = when (this) {
    Relationship.Type.REQUIRED -> ProjectDependencyType.REQUIRED
    Relationship.Type.OPTIONAL -> ProjectDependencyType.OPTIONAL
    Relationship.Type.BUNDLED -> ProjectDependencyType.EMBEDDED
}

internal val ReleaseArtifact.modrinthGameVersions: List<String>?
    get() = gameVersions
        .map { it.lowercase() }
        .filterNot { it.endsWith("-snapshot") }
        .takeIf { it.isNotEmpty() }

internal val ReleaseArtifact.modrinthRelationships: List<ProjectVersion.ProjectDependency>?
    get() = relationships
        .takeIf { it.isNotEmpty() }
        ?.map {
            ProjectVersion.ProjectDependency().apply {
                projectId = it.modrinthId
                dependencyType = it.type.toModrinth()
            }
        }
