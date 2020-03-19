package com.uiza.sdk.helpers;

import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.base.Camera2Base;
import com.pedro.rtplibrary.view.OpenGlView;
import com.uiza.sdk.interfaces.UZCameraChangeListener;
import com.uiza.sdk.interfaces.UZCameraOpenException;
import com.uiza.sdk.interfaces.UZRecordListener;
import com.uiza.sdk.profile.AudioAttributes;
import com.uiza.sdk.profile.VideoAttributes;
import com.uiza.sdk.profile.VideoSize;

import java.io.IOException;
import java.util.List;

public interface ICameraHelper {

    OpenGlView getOpenGlView();

    /**
     * @param reTries retry connect reTries times
     */
    void setConnectReTries(int reTries);

    /**
     * @param uzCameraChangeListener
     */
    void setUZCameraChangeListener(UZCameraChangeListener uzCameraChangeListener);

    /**
     * @param uzRecordListener
     */
    void setUZRecordListener(UZRecordListener uzRecordListener);

    void replaceView(OpenGlView openGlView);

    void replaceView(Context context);

    /**
     * Set filter in position 0.
     *
     * @param filterReader filter to set. You can modify parameters to filter after set it to stream.
     */
    void setFilter(BaseFilterRender filterReader);

    /**
     * Set filter in position 0.
     *
     * @param filterReader filter to set. You can modify parameters to filter after set it to stream.
     */
    void setFilter(int filterPosition, BaseFilterRender filterReader);

    /**
     * Get Anti alias is enabled.
     *
     * @return true is enabled, false is disabled.
     */
    boolean isAAEnabled();

    /**
     * Enable or disable Anti aliasing (This method use FXAA).
     *
     * @param aAEnabled true is AA enabled, false is AA disabled. False by default.
     */
    void enableAA(boolean aAEnabled);

    /**
     * get Stream Width
     */
    int getStreamWidth();

    /**
     * get Stream Height
     */
    int getStreamHeight();

    /**
     * Enable a muted microphone, can be called before, while and after broadcast.
     */
    void enableAudio();


    /**
     * Mute microphone, can be called before, while and after broadcast.
     */

    void disableAudio();

    /**
     * Get mute state of microphone.
     *
     * @return true if muted, false if enabled
     */
    boolean isAudioMuted();

    /**
     * Call this method before use {@link #startBroadCast(String)}. If not you will do a broadcast without audio.
     *
     * @param attrs {@link AudioAttributes}
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a AAC encoder).
     */
    boolean prepareAudio(@NonNull AudioAttributes attrs);

    /**
     * Get video camera state
     *
     * @return true if disabled, false if enabled
     */
    boolean isVideoEnabled();

    /**
     * @param attrs    {@link VideoAttributes}
     * @param rotation could be 90, 180, 270 or 0 (Normally 0 if you are streaming in landscape or 90
     *                 if you are streaming in Portrait). This only affect to stream result. NOTE: Rotation with
     *                 encoder is silence ignored in some devices.
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a H264 encoder).
     */
    boolean prepareVideo(@NonNull VideoAttributes attrs,
                         int rotation
    );

    /**
     * @return list of {@link VideoSize}
     */
    List<VideoSize> getSupportedResolutions();

    /**
     * Need be called after {@link #prepareVideo(VideoAttributes, int)} or/and {@link #prepareAudio(AudioAttributes)}.
     *
     * @param broadCastUrl of the broadcast like: rtmp://ip:port/application/stream_name
     *                     <p>
     *                     RTMP: rtmp://192.168.1.1:1935/fmp4/live_stream_name
     *                     {@link #startPreview(CameraHelper.Facing)} to resolution seated in {@link #prepareVideo(VideoAttributes, int)}.
     *                     If you never startPreview this method {@link #startPreview(CameraHelper.Facing)} for you to resolution seated in {@link #prepareVideo(VideoAttributes, int)}.
     */
    void startBroadCast(String broadCastUrl);

    /**
     * Stop BroadCast started with {@link #startBroadCast(String)}
     */
    void stopBroadCast();

    /**
     * Get broadcast state.
     *
     * @return true if broadcasting, false if not broadcasting.
     */
    boolean isBroadCasting();

    /**
     * Switch camera used. Can be called on preview or while stream, ignored with preview off.
     *
     * @throws UZCameraOpenException If the other camera doesn't support same resolution.
     */
    void switchCamera() throws UZCameraOpenException;

    /**
     * Start camera preview. Ignored, if stream or preview is started.
     * resolution of preview 640x480
     *
     * @param cameraFacing front or back camera. Like: {@link com.pedro.encoder.input.video.CameraHelper.Facing#BACK}
     *                     {@link com.pedro.encoder.input.video.CameraHelper.Facing#FRONT}
     */
    void startPreview(CameraHelper.Facing cameraFacing);

    /**
     * Start camera preview. Ignored, if stream or preview is started.
     *
     * @param cameraFacing front or back camera. Like: {@link com.pedro.encoder.input.video.CameraHelper.Facing#BACK}
     *                     {@link com.pedro.encoder.input.video.CameraHelper.Facing#FRONT}
     * @param width        of preview in px.
     * @param height       of preview in px.
     */
    void startPreview(CameraHelper.Facing cameraFacing, int width, int height);

    /**
     * is Front Camera
     */
    boolean isFrontCamera();

    /**
     * check is on preview
     *
     * @return true if onpreview, false if not preview.
     */
    boolean isOnPreview();

    /**
     * Stop camera preview. Ignored if streaming or already stopped. You need call it after
     *
     * @stopStream to release camera properly if you will close activity.
     */
    void stopPreview();

    /**
     * Get record state.
     *
     * @return true if recording, false if not recoding.
     */
    boolean isRecording();

    /**
     * Start record a MP4 video. Need be called while stream.
     *
     * @param savePath where file will be saved.
     * @throws IOException If you init it before start stream.
     */
    void startRecord(String savePath) throws IOException;

    /**
     * Stop record MP4 video started with @startRecord. If you don't call it file will be unreadable.
     */
    void stopRecord();

    /**
     * Set video bitrate of H264 in kb while stream.
     *
     * @param bitrate H264 in kb.
     */
    void setVideoBitrateOnFly(int bitrate);

    int getBitrate();


    boolean reTry(long delay, String reason);

    /**
     * Check support Flashlight
     * if use Camera1 always return false
     *
     * @return true if support, false if not support.
     */
    boolean isLanternSupported();

    /**
     * @required: <uses-permission android:name="android.permission.FLASHLIGHT"/>
     */
    void enableLantern() throws Exception;

    /**
     * @required: <uses-permission android:name="android.permission.FLASHLIGHT"/>
     */
    void disableLantern();

    boolean isLanternEnabled();

    /**
     * Return max zoom level
     *
     * @return max zoom level
     */
    float getMaxZoom();

    /**
     * Return current zoom level
     *
     * @return current zoom level
     */
    float getZoom();

    /**
     * Set zoomIn or zoomOut to camera.
     * Use this method if you use a zoom slider.
     *
     * @param level Expected to be >= 1 and <= max zoom level
     * @see Camera2Base#getMaxZoom()
     */
    void setZoom(float level);

    /**
     * Set zoomIn or zoomOut to camera.
     *
     * @param event motion event. Expected to get event.getPointerCount() > 1
     */
    void setZoom(MotionEvent event);
}
