package io.uiza.core.helpers

import java.util.concurrent.Executors
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by namnd on 29-Nov-19.
 */

class ThreadHelper {

    val scheduler = Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2) { r ->
        val thread = Thread(r)
        thread.priority = 1
        thread
    })

    private object Holder {
        val INSTANCE = ThreadHelper()
    }

    companion object {
        val instance: ThreadHelper by lazy { Holder.INSTANCE }
    }
}
