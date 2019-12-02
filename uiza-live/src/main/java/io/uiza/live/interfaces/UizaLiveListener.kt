package io.uiza.live.interfaces

interface UizaLiveListener {
    fun onConnectionSuccess()
    fun onConnectionFailed(reason: String?)
    fun onNewBitrate(bitrate: Long)
    fun onDisconnect()
    fun onAuthError()
    fun onAuthSuccess()
    fun surfaceCreated()
    fun surfaceChanged(
        format: Int, width: Int,
        height: Int
    )
    fun surfaceDestroyed()
}