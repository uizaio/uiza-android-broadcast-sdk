package com.uiza.sdkbroadcast.helpers;

import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.base.Camera2Base;
import com.pedro.rtplibrary.view.OpenGlView;
import com.uiza.sdkbroadcast.interfaces.UZCameraChangeListener;
import com.uiza.sdkbroadcast.interfaces.UZCameraOpenException;
import com.uiza.sdkbroadcast.interfaces.UZRecordListener;
import com.uiza.sdkbroadcast.interfaces.UZTakePhotoCallback;
import com.uiza.sdkbroadcast.profile.AudioAttributes;
import com.uiza.sdkbroadcast.profile.VideoAttributes;
import com.uiza.sdkbroadcast.profile.VideoSize;

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

    void setVideoAttributes(VideoAttributes attributes);

    void setAudioAttributes(AudioAttributes attributes);

    void setLandscape(boolean landscape);

    /**
     * Set filter in position 0.
     *
     * @param filterReader filter to set. You can modify parameters to filter after set it to stream.
     */
    void setFilter(BaseFilterRender filterReader);

    /**
     * @param filterPosition position of filter
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
     * You will do a portrait broadcast
     *
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a H264 encoder).
     */
    boolean prepareBroadCast();


    /**
     * @param isLandscape boolean
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a H264 encoder).
     */
    boolean prepareBroadCast(boolean isLandscape);

    /**
     * Call this method before use {@link #startBroadCast(String)}.
     *
     * @param audioAttributes {@link AudioAttributes} If null you will do a broadcast without audio.
     * @param videoAttributes {@link VideoAttributes}
     * @param isLandscape     boolean you will broadcast is landscape
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a AAC encoder).
     */
    boolean prepareBroadCast(AudioAttributes audioAttributes, @NonNull VideoAttributes videoAttributes, boolean isLandscape);


    /**
     * Get video camera state
     *
     * @return true if disabled, false if enabled
     */
    boolean isVideoEnabled();

    /**
     * Need be called after {@link #prepareBroadCast(AudioAttributes, VideoAttributes, boolean)} or/and {@link #prepareBroadCast(boolean)}.
     *
     * @param broadCastUrl of the broadcast like: rtmp://ip:port/application/stream_name
     *                     <p>
     *                     RTMP: rtmp://192.168.1.1:1935/fmp4/live_stream_name
     *                     {@link #startPreview(CameraHelper.Facing)} to resolution seated in
     *                     {@link #prepareBroadCast(AudioAttributes, VideoAttributes, boolean)}.
     *                     If you never startPreview this method {@link #startPreview(CameraHelper.Facing)} for you to resolution seated in
     *                     {@link #prepareBroadCast(AudioAttributes, VideoAttributes, boolean)}.
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
     * @return list of {@link VideoSize}
     */
    List<VideoSize> getSupportedResolutions();


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
     * take a photo
     * @param callback {@link UZTakePhotoCallback}
     */
    void takePhoto(UZTakePhotoCallback callback);

    /**
     * Set video bitrate of H264 in kb while stream.
     *
     * @param bitrate H264 in kb.
     */
    void setVideoBitrateOnFly(int bitrate);

    /**
     *
     * @return bitrate in kps
     */
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
