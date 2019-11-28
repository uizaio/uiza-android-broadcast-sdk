package io.uiza.player.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.view.Surface
import android.view.WindowManager


object ScreenUtil {

    private val TAG: String = ScreenUtil::class.java.simpleName

    @JvmStatic
    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    @JvmStatic
    fun getScreenHeightIncludeNavigationBar(context: Context): Int {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val outPoint = Point()
        display.getRealSize(outPoint)
        return if (outPoint.y > outPoint.x) {
            outPoint.y
        } else {
            outPoint.x
        }
    }

    @JvmStatic
    fun isFullScreen(context: Context): Boolean {
        val rotation =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                .rotation
        return when (rotation) {
            Surface.ROTATION_0 -> false
            Surface.ROTATION_90 -> true
            Surface.ROTATION_180 -> false
            else -> true
        }
    }
}