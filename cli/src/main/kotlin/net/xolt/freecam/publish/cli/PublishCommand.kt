package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.main
import net.xolt.freecam.publish.PublisherFactory

@JvmInline
value class PublishCommand internal constructor(
    private val cmd: PublishCliCommand
) {

    constructor(publisherFactory: PublisherFactory) : this(PublishCliCommand(
        publisherFactory = publisherFactory,
    ))

    suspend fun main(args: Array<String>) = cmd.main(args)
}