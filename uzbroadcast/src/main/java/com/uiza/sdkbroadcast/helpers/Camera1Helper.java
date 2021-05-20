package com.uiza.sdkbroadcast.helpers;

import android.content.Context;
import android.hardware.Camera;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.view.OpenGlView;
import com.uiza.sdkbroadcast.enums.RecordStatus;
import com.uiza.sdkbroadcast.interfaces.UZCameraChangeListener;
import com.uiza.sdkbroadcast.interfaces.UZCameraOpenException;
import com.uiza.sdkbroadcast.interfaces.UZRecordListener;
import com.uiza.sdkbroadcast.interfaces.UZTakePhotoCallback;
import com.uiza.sdkbroadcast.profile.AudioAttributes;
import com.uiza.sdkbroadcast.profile.VideoAttributes;
import com.uiza.sdkbroadcast.profile.VideoSize;
import com.uiza.sdkbroadcast.util.ListUtils;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

/**
 * Wrapper to stream with camera1 api and microphone. Support stream with OpenGlView(Custom SurfaceView that use OpenGl).
 * OpenGlView use Surface to buffer mode(This mode is generally
 * better because skip buffer processing).
 *
 * API requirements:
 * OpenGlView: API 18+.
 *
 * Created by namnd on 10/01/20.
 */
public class Camera1Helper implements ICameraHelper {

    private RtmpCamera1 rtmpCamera1;

    private UZCameraChangeListener uzCameraChangeListener;

    private UZRecordListener uzRecordListener;
    private OpenGlView openGlView;

    /**
     * VideoAttributes
     */
    private VideoAttributes videoAttributes;
    /**
     * AudioAttributes
     */
    private AudioAttributes audioAttributes;

    private boolean isLandscape = false;

    public Camera1Helper(@NonNull OpenGlView openGlView, ConnectCheckerRtmp connectCheckerRtmp) {
        this.rtmpCamera1 = new RtmpCamera1(openGlView, connectCheckerRtmp);
        this.openGlView = openGlView;
    }

    @Override
    public OpenGlView getOpenGlView() {
        return openGlView;
    }

    @Override
    public void replaceView(OpenGlView openGlView) {
        this.openGlView = openGlView;
        rtmpCamera1.replaceView(openGlView);
    }

    @Override
    public void replaceView(Context context) {
        this.openGlView = null;
        rtmpCamera1.replaceView(context);
    }

    @Override
    public void setVideoAttributes(VideoAttributes videoAttributes) {
        this.videoAttributes = videoAttributes;
    }

    @Override
    public void setAudioAttributes(AudioAttributes audioAttributes) {
        this.audioAttributes = audioAttributes;
    }

    @Override
    public void setLandscape(boolean landscape) {
        this.isLandscape = landscape;
    }

    @Override
    public void setConnectReTries(int reTries) {
        rtmpCamera1.setReTries(reTries);
    }

    @Override
    public boolean reTry(long delay, @NonNull String reason) {
        return rtmpCamera1.reTry(delay, reason);
    }

    @Override
    public void setUZCameraChangeListener(@NonNull UZCameraChangeListener uzCameraChangeListener) {
        this.uzCameraChangeListener = uzCameraChangeListener;
    }

    @Override
    public void setUZRecordListener(UZRecordListener uzRecordListener) {
        this.uzRecordListener = uzRecordListener;
    }


    @Override
    public void setFilter(@NotNull BaseFilterRender filterReader) {
        rtmpCamera1.getGlInterface().setFilter(filterReader);
    }

    @Override
    public void setFilter(int filterPosition, @NotNull BaseFilterRender filterReader) {
        rtmpCamera1.getGlInterface().setFilter(filterPosition, filterReader);
    }

    @Override
    public void enableAA(boolean aAEnabled) {
        rtmpCamera1.getGlInterface().enableAA(aAEnabled);
    }

    @Override
    public boolean isAAEnabled() {
        return rtmpCamera1.getGlInterface().isAAEnabled();
    }

    @Override
    public int getStreamWidth() {
        return rtmpCamera1.getStreamHeight();
    }

    @Override
    public int getStreamHeight() {
        return rtmpCamera1.getStreamWidth();
    }

    @Override
    public void enableAudio() {
        rtmpCamera1.enableAudio();
    }

    @Override
    public void disableAudio() {
        rtmpCamera1.disableAudio();
    }

    @Override
    public boolean isAudioMuted() {
        return rtmpCamera1.isAudioMuted();
    }

    @Override
    public boolean prepareBroadCast() {
        return prepareBroadCast(isLandscape);
    }

    @Override
    public boolean prepareBroadCast(boolean isLandscape) {
        if (videoAttributes == null) {
            Timber.e("Please set videoAttributes");
            return false;
        }
        return prepareBroadCast(audioAttributes, videoAttributes, isLandscape);
    }

    @Override
    public boolean prepareBroadCast(AudioAttributes audioAttributes, @NonNull VideoAttributes videoAttributes, boolean isLandscape) {
        this.audioAttributes = audioAttributes;
        this.videoAttributes = videoAttributes;
        this.isLandscape = isLandscape;
        return (audioAttributes == null) ? prepareVideo(videoAttributes, isLandscape ? 0 : 90) :
                prepareAudio(audioAttributes) && prepareVideo(videoAttributes, isLandscape ? 0 : 90);
    }

    private boolean prepareAudio(@NonNull AudioAttributes attrs) {
        return rtmpCamera1.prepareAudio(
                attrs.getBitRate(),
                attrs.getSampleRate(),
                attrs.isStereo(),
                attrs.isEchoCanceler(),
                attrs.isNoiseSuppressor()
        );
    }

    @Override
    public boolean isVideoEnabled() {
        return rtmpCamera1.isVideoEnabled();
    }

    private boolean prepareVideo(@NotNull VideoAttributes attrs, int rotation) {
        return rtmpCamera1.prepareVideo(attrs.getSize().getWidth(),
                attrs.getSize().getHeight(),
                attrs.getFrameRate(),
                attrs.getBitRate(),
                false,
                attrs.getFrameInterval(),
                rotation,
                attrs.getAVCProfile(),
                attrs.getAVCProfileLevel()
        );
    }

    @Override
    public void startBroadCast(@NotNull String broadCastUrl) {
        rtmpCamera1.startStream(broadCastUrl);
    }

    @Override
    public void stopBroadCast() {
        rtmpCamera1.stopStream();
    }

    @Override
    public boolean isBroadCasting() {
        return rtmpCamera1.isStreaming();
    }

    @Override
    public void setVideoBitrateOnFly(int bitrate) {
        rtmpCamera1.setVideoBitrateOnFly(bitrate);
    }

    @Override
    public int getBitrate() {
        return rtmpCamera1.getBitrate();
    }

    @Override
    public boolean isFrontCamera() {
        return rtmpCamera1.isFrontCamera();
    }

    @Override
    public void switchCamera() throws UZCameraOpenException {
        try {
            rtmpCamera1.switchCamera();
        } catch (CameraOpenException e) {
            throw new UZCameraOpenException(e.getMessage());
        }
        if (uzCameraChangeListener != null)
            uzCameraChangeListener.onCameraChange(rtmpCamera1.isFrontCamera());
    }

    @Override
    public List<VideoSize> getSupportedResolutions() {
        List<Camera.Size> sizes;
        if (rtmpCamera1.isFrontCamera()) {
            sizes = rtmpCamera1.getResolutionsFront();
        } else {
            sizes = rtmpCamera1.getResolutionsBack();
        }
        return ListUtils.map(sizes, VideoSize::fromSize);
    }

    @Override
    public void startPreview(@NotNull CameraHelper.Facing cameraFacing) {
        // because portrait
        rtmpCamera1.startPreview(cameraFacing, 480, 854);
    }

    @Override
    public void startPreview(@NotNull CameraHelper.Facing cameraFacing, int w, int h) {
        // because portrait
        rtmpCamera1.startPreview(cameraFacing, h, w);
    }

    @Override
    public boolean isOnPreview() {
        return rtmpCamera1.isOnPreview();
    }

    @Override
    public void stopPreview() {
        rtmpCamera1.stopPreview();
    }

    @Override
    public boolean isRecording() {
        return rtmpCamera1.isRecording();
    }

    @Override
    public void startRecord(@NotNull String savePath) throws IOException {
        if (uzRecordListener != null)
            rtmpCamera1.startRecord(savePath, status -> uzRecordListener.onStatusChange(RecordStatus.lookup(status)));
        else
            rtmpCamera1.startRecord(savePath);
    }

    @Override
    public void stopRecord() {
        rtmpCamera1.stopRecord();
        rtmpCamera1.startPreview();
    }

    @Override
    public void takePhoto(@NonNull UZTakePhotoCallback callback) {
        rtmpCamera1.getGlInterface().takePhoto(callback::onTakePhoto);
    }

    @Override
    public boolean isLanternSupported() {
        return false;
    }

    @Override
    public void enableLantern() throws Exception {
        rtmpCamera1.enableLantern();
    }

    @Override
    public void disableLantern() {
        rtmpCamera1.disableLantern();
    }

    @Override
    public boolean isLanternEnabled() {
        return rtmpCamera1.isLanternEnabled();
    }

    @Override
    public float getMaxZoom() {
        return 1.0f;
    }

    @Override
    public float getZoom() {
        return 1.0f;
    }

    @Override
    public void setZoom(float level) {

    }

    @Override
    public void setZoom(@NotNull MotionEvent event) {
        rtmpCamera1.setZoom(event);
    }
}
