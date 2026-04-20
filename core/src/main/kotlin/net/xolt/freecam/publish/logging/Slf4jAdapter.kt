package net.xolt.freecam.publish.logging

import org.slf4j.Marker
import org.slf4j.helpers.AbstractLogger
import org.slf4j.helpers.MessageFormatter
import org.slf4j.event.Level as Slf4jLevel

val Logger.slf4j: org.slf4j.Logger
    get() = Slf4jAdapter(this)

private val Slf4jLevel?.asLogLevel: LogLevel
    get() = when (this) {
        Slf4jLevel.ERROR -> LogLevel.ERROR
        Slf4jLevel.WARN -> LogLevel.WARNING
        Slf4jLevel.INFO -> LogLevel.INFO
        Slf4jLevel.DEBUG -> LogLevel.DEBUG
        Slf4jLevel.TRACE -> LogLevel.TRACE
        null -> LogLevel.NONE
    }

private class Slf4jAdapter(private val delegate: Logger) : AbstractLogger() {

    init {
        name = delegate::class.qualifiedName
    }

    override fun getFullyQualifiedCallerName() = null

    override fun handleNormalizedLoggingCall(
        level: Slf4jLevel,
        marker: Marker?,
        messagePattern: String,
        arguments: Array<out Any?>?,
        throwable: Throwable?,
    ) = delegate.log(level.asLogLevel) {
        MessageFormatter.arrayFormat(messagePattern, arguments, throwable).run {
            this.throwable?.let { "$message: $it" } ?: message
        }
    }

    override fun isTraceEnabled() = with(delegate) {
        LogLevel.TRACE.enabled
    }

    override fun isDebugEnabled() = with(delegate) {
        LogLevel.DEBUG.enabled
    }

    override fun isInfoEnabled() = with(delegate) {
        LogLevel.INFO.enabled
    }

    override fun isWarnEnabled() = with(delegate) {
        LogLevel.WARNING.enabled
    }

    override fun isErrorEnabled() = with(delegate) {
        LogLevel.ERROR.enabled
    }

    override fun isTraceEnabled(marker: Marker?) = isTraceEnabled
    override fun isDebugEnabled(marker: Marker?) = isDebugEnabled
    override fun isInfoEnabled(marker: Marker?) = isInfoEnabled
    override fun isWarnEnabled(marker: Marker?) = isWarnEnabled
    override fun isErrorEnabled(marker: Marker?) = isErrorEnabled
}
