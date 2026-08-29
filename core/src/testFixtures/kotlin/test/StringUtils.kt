package net.xolt.freecam.test

import kotlin.io.path.writeText

fun String.toTestFile() = createTestFile().also { file ->
    file.writeText(this)
}
