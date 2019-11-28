package io.uiza.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

inline fun <reified T : Activity> Activity.lauchActivity(
    requestCode: Int = -1,
    finish: Boolean = false,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    if(requestCode > -1) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivity(intent, options)
        if (finish) this.finish()
    }

}

inline fun <reified T : Activity> Context.lauchActivity(
    finish: Boolean = false,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
    if (finish) (this as Activity).finish()
}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)

fun Activity.showOnUiThread(init: Activity.() -> Unit): Activity {
    if (!isFinishing) {
        runOnUiThread {
            init()
        }
    }
    return this
}