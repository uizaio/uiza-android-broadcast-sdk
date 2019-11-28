package io.uiza.core.utils

import androidx.annotation.NonNull
import io.sentry.Sentry
import io.sentry.event.Event

import io.sentry.event.EventBuilder


object SentryUtil {

    @JvmStatic
    fun captureException(@NonNull ex: Throwable) {
        Sentry.capture(ex)
    }

    @JvmStatic
    fun captureEvent(@NonNull message: String) {
        val event: Event = EventBuilder().withMessage(message).build()
        Sentry.capture(event)
    }
}