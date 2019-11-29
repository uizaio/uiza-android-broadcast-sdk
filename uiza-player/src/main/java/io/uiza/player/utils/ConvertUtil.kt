@file:JvmName("ConvertKt")

package io.uiza.player.utils

import android.content.res.Resources
import java.text.DecimalFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

fun Float.dp2px(): Int {
    val scale =
        Resources.getSystem().displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Long.humanReadableByteCount(
    si: Boolean,
    isBits: Boolean
): String? {
    val unit = if (!si) 1000 else 1024
    if (this < unit) return "$this KB"
    val exp =
        (ln(this.toDouble()) / ln(unit.toDouble())).toInt()
    val pre =
        (if (si) "kMGTPE" else "KMGTPE")[exp - 1].toString() + if (si) "" else "i"
    return if (isBits) java.lang.String.format(
        Locale.getDefault(),
        "%.1f %sb",
        this / unit.toDouble().pow(exp.toDouble()),
        pre
    ) else java.lang.String.format(
        Locale.getDefault(),
        "%.1f %sB",
        this / unit.toDouble().pow(exp.toDouble()),
        pre
    )
}

fun Double.bandwidthFormat(): String {
    return if (this < 1e6) {
        String.format(
            Locale.US,
            "%s Kbps",
            (this / 10.0.pow(3.0)).formatDouble(2)
        )
    } else {
        String.format(
            Locale.US,
            "%s Mbps",
            (this / 10.0.pow(6.0)).formatDouble(2)
        )
    }
}

fun Double.formatDouble(precision: Int): String {
    return DecimalFormat(
        "#0." + if (precision <= 1) "0" else if (precision == 2) "00" else "000"
    ).format(this)
}
