@file:JvmName("SentryKt")

package io.uiza.core.utils

import io.sentry.Sentry
import io.sentry.event.Event
import io.sentry.event.EventBuilder

fun captureException(ex: Throwable) {
    Sentry.capture(ex)
}

fun captureEvent(message: String) {
    val event: Event = EventBuilder().withMessage(message).build()
    Sentry.capture(event)
}