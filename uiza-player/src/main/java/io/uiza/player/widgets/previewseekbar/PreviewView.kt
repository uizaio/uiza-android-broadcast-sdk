package io.uiza.player.widgets.previewseekbar

import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

interface PreviewView {
    fun getProgress(): Int
    fun getMax(): Int
    fun getThumbOffset(): Int
    fun getDefaultColor(): Int
    fun isShowingPreview(): Boolean
    fun showPreview()
    fun hidePreview()
    fun setPreviewLoader(previewLoader: PreviewLoader?)
    fun setPreviewColorTint(@ColorInt color: Int)
    fun setPreviewColorResourceTint(@ColorRes color: Int)
    fun attachPreviewFrameLayout(frameLayout: FrameLayout?)
    fun addOnPreviewChangeListener(listener: OnPreviewChangeListener?)
    fun removeOnPreviewChangeListener(listener: OnPreviewChangeListener?)
    interface OnPreviewChangeListener {
        fun onStartPreview(previewView: PreviewView?, progress: Int)
        fun onStopPreview(previewView: PreviewView?, progress: Int)
        fun onPreview(
            previewView: PreviewView?,
            progress: Int,
            fromUser: Boolean
        )
    }
}