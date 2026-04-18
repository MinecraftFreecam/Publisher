package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.main
import net.xolt.freecam.publish.PublisherFactory

@JvmInline
value class PublishCommand internal constructor(
    private val cmd: PublishCliCommand
) {

    constructor(version: String, publisherFactory: PublisherFactory) : this(PublishCliCommand(
        version = version,
        publisherFactory = publisherFactory,
    ))

    suspend fun main(args: Array<String>) = cmd.main(args)
}
