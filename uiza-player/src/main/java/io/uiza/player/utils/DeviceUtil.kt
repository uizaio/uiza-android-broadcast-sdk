package io.uiza.player.utils

import android.app.Activity
import android.app.UiModeManager
import android.content.ClipData
import android.content.Context
import android.content.Context.UI_MODE_SERVICE
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.provider.Settings.System.ACCELEROMETER_ROTATION
import android.provider.Settings.canDrawOverlays
import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.annotation.RequiresPermission
import io.uiza.core.utils.SentryUtil
import java.util.*


object DeviceUtil {

    private val TAG: String = DeviceUtil::class.java.simpleName
    private const val COPY_LABEL = "Copy"

    const val RATIO_LAND_TABLET = 24
    const val RATIO_PORTRAIT_TABLET = 20

    const val RATIO_LAND_MOBILE = 18
    const val RATIO_PORTRAIT_MOBILE = 12

    @JvmStatic
    fun isTablet(activity: Activity): Boolean {
        return activity.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    @JvmStatic
    fun isNavigationBarAvailable(): Boolean {
        val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        val hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)
        return !(hasBackKey && hasHomeKey)
    }

    @JvmStatic
    fun getCurrentAndroidVersion(activity: Activity): Long {
        var thisVersion: Long
        try {
            val pi =
                activity.packageManager.getPackageInfo(activity.packageName, 0)
            thisVersion = pi.longVersionCode
        } catch (e: PackageManager.NameNotFoundException) {
            thisVersion = 1
            SentryUtil.captureException(e)
        }
        return thisVersion
    }

    @JvmStatic
    fun setClipboard(context: Context, text: String?) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText(COPY_LABEL, text)
        clipboard.setPrimaryClip(clip)
//        Toast.show(context, "Copied!")
    }

    @JvmStatic
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun vibrate(context: Context, milliseconds: Long) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(
                VibrationEffect.createOneShot(
                    milliseconds,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        else
            v.vibrate(milliseconds)
    }

    @JvmStatic
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun vibrate(context: Context) {
        vibrate(context, 300)
    }

    @JvmStatic
    fun getRandomNumber(max: Int): Int {
        val r = Random()
        return r.nextInt(max)
    }

    @JvmStatic
    fun isTablet(context: Context): Boolean {
        return ((context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    @JvmStatic
    fun isTV(context: Context): Boolean {
        val uiModeManager =
            context.getSystemService(UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }

    //return true if device is set auto switch rotation on
//return false if device is set auto switch rotation off
    @JvmStatic
    fun isRotationPossible(context: Context): Boolean {
        val hasAccelerometer: Boolean = context.packageManager
            .hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)
        return hasAccelerometer && Settings.System.getInt(
            context.contentResolver,
            ACCELEROMETER_ROTATION,
            0
        ) == 1
    }

    @JvmStatic
    fun isCanOverlay(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || canDrawOverlays(context)
    }
}