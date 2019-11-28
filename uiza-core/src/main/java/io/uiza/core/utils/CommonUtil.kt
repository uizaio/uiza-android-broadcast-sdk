@file:JvmName("CommonKt")

package io.uiza.core.utils

import android.os.Build


/**
 * November 2014: Lollipop!
 * API 21 or Above
 */
fun isLlAndAbove() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)

/**
 * November 2014: Lollipop!
 * API 21 or Above
 */
inline fun llAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        block()
    }
}

/**
 * March 2015: Lollipop with an extra sugar coating on the outside!
 * API 22 or Above
 */
fun isLlMr1AndAbove() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)

/**
 * March 2015: Lollipop with an extra sugar coating on the outside!
 * API 22 or Above
 */
inline fun llMr1AndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        block()
    }
}


/**
 * M is for Marshmallow!
 * API 23 or Above
 */

fun isMmAndAbove() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

/**
 * M is for Marshmallow!
 * API 23 or Above
 */
inline fun mmAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        block()
    }
}

/**
 * N is for Nougat.
 * API 24 or Above
 */

fun isNougatAndAbove() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)

/**
 * N is for Nougat.
 * API 24 or Above
 */
inline fun nougatAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        block()
    }
}

/**
 * N MR1: Nougat++.
 * API 25 or Above
 */
fun isNougatMr1AndAbove() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)

/**
 * N MR1: Nougat++.
 * API 25 or Above
 */
inline fun nougatMr1AndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        block()
    }
}
/**
 * O is for Oreo
 * API 26 or Above
 */

fun isOreoAndAbove() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

/**
 * O is for Oreo: android 8
 * API 26 or Above
 */
inline fun oreoAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        block()
    }
}

/**
 * O MR1: Oreo++ : android 8.1
 * API 27 or Above
 */
fun isOreoMr1AndAbove() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
/**
 * O MR1: Oreo++.
 * API 27 or Above
 */
inline fun oreoMr1AndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        block()
    }
}

/**
 * P is for Pie: android 9
 * API 28 or Above
 */
fun isPieAndAbove() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)

/**
 * P is for Pie: android 9
 * API 28 or Above
 */
inline fun pieAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        block()
    }
}

/**
 * Q: android 10
 * API 29 or Above
 */

fun isQAndAbove() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)

/**
 * Q: android 10
 * API 29 or Above
 */

inline fun qAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        block()
    }
}
