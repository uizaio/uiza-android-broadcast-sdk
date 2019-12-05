package io.uiza.live.interfaces

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.encoder.input.video.CameraOpenException
import com.pedro.rtplibrary.util.RecordController
import java.io.IOException

interface ICameraHelper {

    fun setCameraChangeListener(cameraChangeListener: CameraChangeListener)

    /**
     * Set filter in position 0.
     * @param filterReader filter to set. You can modify parameters to filter after set it to stream.
     */
    fun setFilter(filterReader: BaseFilterRender)

    /**
     * Set filter in position 0.
     * @param filterReader filter to set. You can modify parameters to filter after set it to stream.
     */
    fun setFilter(filterPosition: Int, filterReader: BaseFilterRender)

    /**
     * Get Anti alias is enabled.
     * @return true is enabled, false is disabled.
     */
    fun isAAEnabled(): Boolean

    /**
     * Enable or disable Anti aliasing (This method use FXAA).
     *
     * @param AAEnabled true is AA enabled, false is AA disabled. False by default.
     */
    fun enableAA(aAEnabled: Boolean)

    /**
     * get Stream Width
     */
    fun getStreamWidth(): Int

    /**
     * get Stream Height
     */
    fun getStreamHeight(): Int

    /**
     * Same to call: prepareAudio(64 * 1024, 32000, true, false, false);
     *
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a AAC encoder).
     */
    fun prepareAudio(): Boolean

    /**
     * Same to call: prepareAudio(64 * 1024, 32000, true, false, false);
     *
     * @param bitrate AAC in kb.
     * @param sampleRate of audio in hz. Can be 8000, 16000, 22500, 32000, 44100.
     * @param isStereo true if you want Stereo audio (2 audio channels), false if you want Mono audio
     * (1 audio channel).
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a AAC encoder).
     */
    fun prepareAudio(bitrate: Int, sampleRate: Int, isStereo: Boolean): Boolean

    /**
     * Use profle
     * prepareVideo(profile, 24, 2, 90);
     *
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a H264 encoder).
     */

    fun prepareVideo(profile: ProfileEncode): Boolean

    /**
     * @param fps frames per second of the stream.
     * @param rotation could be 90, 180, 270 or 0 (Normally 0 if you are streaming in landscape or 90
     * if you are streaming in Portrait). This only affect to stream result. NOTE: Rotation with
     * encoder is silence ignored in some devices.
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a H264 encoder).
     */
    fun prepareVideo(profile: ProfileEncode, fps: Int, iFrameInterval: Int, rotation: Int): Boolean

    /**
     * Need be called after @prepareVideo or/and @prepareAudio. This method override resolution of
     *
     * @param liveEndpoint of the stream like: rtmp://ip:port/application/stream_name
     *
     * RTMP: rtmp://192.168.1.1:1935/fmp4/live_stream_name
     * @startPreview to resolution seated in @prepareVideo. If you never startPreview this method
     * startPreview for you to resolution seated in @prepareVideo.
     */
    fun startStream(liveEndpoint: String)

    /**
     * Stop stream started with @startStream.
     */
    fun stopStream()

    /**
     * Get stream state.
     *
     * @return true if streaming, false if not streaming.
     */
    fun isStreaming(): Boolean

    /**
     * Switch camera used. Can be called on preview or while stream, ignored with preview off.
     *
     * @throws CameraOpenException If the other camera doesn't support same resolution.
     */
    @Throws(UizaCameraOpenException::class)
    fun switchCamera()

    fun startPreview(cameraFacing: CameraHelper.Facing)
    /**
     * Start preview
     */
    fun startPreview(cameraFacing: CameraHelper.Facing, width: Int, height: Int)

    /**
     * is Front Camera
     */
    fun isFrontCamera(): Boolean

    /**
     * Stop camera preview. Ignored if streaming or already stopped. You need call it after
     *
     * @stopStream to release camera properly if you will close activity.
     */
    fun stopPreview()

    /**
     * Get record state.
     *
     * @return true if recording, false if not recoding.
     */
    fun isRecording(): Boolean

    /**
     * Start record a MP4 video. Need be called while stream.
     *
     * @param savePath where file will be saved.
     * @throws IOException If you init it before start stream.
     */
    @Throws(IOException::class)
    fun startRecord(savePath: String, listener: RecordController.Listener? = null)

    /**
     * Stop record MP4 video started with @startRecord. If you don't call it file will be unreadable.
     */
    fun stopRecord()

    /**
     * Set video bitrate of H264 in kb while stream.
     *
     * @param bitrate H264 in kb.
     */
    fun setVideoBitrateOnFly(bitrate: Int)

    fun getBitrate(): Int

    fun shouldRetry(reason: String): Boolean

    fun reTry(delay: Long)
}
