@file:JvmName("DeviceKt")

package io.uiza.player.utils

import android.app.Activity
import android.app.UiModeManager
import android.content.ClipData
import android.content.Context
import android.content.Context.UI_MODE_SERVICE
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.provider.Settings.System.ACCELEROMETER_ROTATION
import android.provider.Settings.canDrawOverlays
import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.annotation.RequiresPermission
import androidx.core.content.pm.PackageInfoCompat
import java.util.*


private const val COPY_LABEL = "Copy"

const val RATIO_LAND_TABLET = 24
const val RATIO_PORTRAIT_TABLET = 20

const val RATIO_LAND_MOBILE = 18
const val RATIO_PORTRAIT_MOBILE = 12


//@JvmName("isTablet")
fun Context.isTablet(): Boolean {
    return resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}


fun Context.isTV(): Boolean {
    val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}

//return true if device is set auto switch rotation on
//return false if device is set auto switch rotation off
fun Context.isRotationPossible(): Boolean {
    val hasAccelerometer: Boolean = packageManager
        .hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)
    return hasAccelerometer && Settings.System.getInt(
        contentResolver,
        ACCELEROMETER_ROTATION,
        0
    ) == 1
}

fun Context.isCanOverlay(): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || canDrawOverlays(this)
}

fun Context.currentVolume(streamType: Int): Int {
    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    return am.getStreamVolume(streamType)
}

fun Context.maxVolume(streamType: Int): Int {
    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    return am.getStreamMaxVolume(streamType)
}

fun deviceInfo(): String {
    return Build.MODEL + " / Android " + Build.VERSION.RELEASE + " (SDK: " + Build.VERSION.SDK_INT + ")"
}

//@JvmName("currentAndroidVersion")
fun Activity.currentAndroidVersion(): Long {
    var thisVersion: Long
    thisVersion = try {
        val pi = packageManager.getPackageInfo(packageName, 0)
        PackageInfoCompat.getLongVersionCode(pi)
    } catch (e: PackageManager.NameNotFoundException) {
        1
    }
    return thisVersion
}

fun Context.setClipboard(text: String?) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = ClipData.newPlainText(COPY_LABEL, text)
    clipboard.setPrimaryClip(clip)
//        Toast.show(context, "Copied!")
}

@RequiresPermission(android.Manifest.permission.VIBRATE)
fun Context.vibrate(milliseconds: Long) {
    val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        v.vibrate(
            VibrationEffect.createOneShot(
                milliseconds,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    else {
        //deprecated in API 26
        v.vibrate(milliseconds)
    }
}

@RequiresPermission(android.Manifest.permission.VIBRATE)
fun Context.vibrate() {
    vibrate(300)
}

fun getRandomNumber(max: Int): Int {
    val r = Random()
    return r.nextInt(max)
}

fun isNavigationBarAvailable(): Boolean {
    val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
    val hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)
    return !(hasBackKey && hasHomeKey)
}