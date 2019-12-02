@file:JvmName("LiveKt")

package io.uiza.live

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

fun View.blinking() {
    val anim: Animation = AlphaAnimation(0.0f, 1.0f)
    anim.duration = 1000
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE
    this.startAnimation(anim)
}
