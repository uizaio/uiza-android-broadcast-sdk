@file:JvmName("ScreenKt")

package io.uiza.player.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.view.Surface
import android.view.View
import android.view.WindowManager


private val TAG: String = "ScreenKt"

val screenWidth = Resources.getSystem().displayMetrics.widthPixels


fun Context.screenHeightIncludeNavigationBar(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val outPoint = Point()
    display.getRealSize(outPoint)
    return if (outPoint.y > outPoint.x) {
        outPoint.y
    } else {
        outPoint.x
    }
}

fun Context.isFullScreen(): Boolean {
    return when ((getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation) {
        Surface.ROTATION_0 -> false
        Surface.ROTATION_90 -> true
        Surface.ROTATION_180 -> false
        else -> true
    }
}

fun Activity.toggleScreenOritation() {
    val orientation: Int = resources.configuration.orientation
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }
}

fun Activity.hideSystemUI() { // Enables regular immersive mode.
// For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
// Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    window.decorView.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
// content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

// Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
fun Activity.showSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}