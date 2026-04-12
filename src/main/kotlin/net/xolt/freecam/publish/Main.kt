package net.xolt.freecam.publish

import net.xolt.freecam.publish.cli.PublishCommand

suspend fun main(args: Array<String>) = PublishCommand(
    publisherFactory = DefaultPublisherFactory,
).main(args)
