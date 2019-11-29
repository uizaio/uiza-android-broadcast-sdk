package io.uiza.player.widgets.previewseekbar

interface PreviewLoader {
    fun loadPreview(currentPosition: Long, max: Long)
}