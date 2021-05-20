package com.uiza.sdkbroadcast.helpers;

import android.content.Context;
import android.os.Build;
import android.util.Size;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
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

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

/**
 * Wrapper to stream with camera2 api and microphone. Support stream with OpenGlView(Custom SurfaceView that use OpenGl) and Context(background mode).
 * All views use Surface to buffer encoding mode for H264.
 *
 * API requirements:
 * API 21+.
 *
 * Created by namnd on 10/01/20.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Helper implements ICameraHelper {

    private RtmpCamera2 rtmpCamera2;

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

    public Camera2Helper(@NonNull OpenGlView openGlView, ConnectCheckerRtmp connectCheckerRtmp) {
        this.rtmpCamera2 = new RtmpCamera2(openGlView, connectCheckerRtmp);
        this.openGlView = openGlView;
    }

    @Override
    public OpenGlView getOpenGlView() {
        return openGlView;
    }

    @Override
    public void replaceView(Context context) {
        this.openGlView = null;
        rtmpCamera2.replaceView(context);
    }

    @Override
    public void replaceView(OpenGlView openGlView) {
        if (this.openGlView == null)
            rtmpCamera2.replaceView(openGlView);
    }

    @Override
    public void setConnectReTries(int reTries) {
        rtmpCamera2.setReTries(reTries);
    }

    @Override
    public boolean reTry(long delay, @NonNull String reason) {
        return rtmpCamera2.reTry(delay, reason);
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
    public void setUZCameraChangeListener(@NonNull UZCameraChangeListener uzCameraChangeListener) {
        this.uzCameraChangeListener = uzCameraChangeListener;
    }

    @Override
    public void setUZRecordListener(UZRecordListener uzRecordListener) {
        this.uzRecordListener = uzRecordListener;
    }

    @Override
    public void setFilter(@NonNull BaseFilterRender filterReader) {
        rtmpCamera2.getGlInterface().setFilter(filterReader);
    }

    @Override
    public void setFilter(int filterPosition, @NonNull BaseFilterRender filterReader) {
        rtmpCamera2.getGlInterface().setFilter(filterPosition, filterReader);
    }

    @Override
    public void enableAA(boolean aAEnabled) {
        rtmpCamera2.getGlInterface().enableAA(aAEnabled);
    }

    @Override
    public boolean isAAEnabled() {
        return rtmpCamera2.getGlInterface().isAAEnabled();
    }

    @Override
    public void setVideoBitrateOnFly(int bitrate) {
        rtmpCamera2.setVideoBitrateOnFly(bitrate);
    }

    @Override
    public int getBitrate() {
        return rtmpCamera2.getBitrate();
    }

    @Override
    public int getStreamWidth() {
        return rtmpCamera2.getStreamWidth();
    }

    @Override
    public int getStreamHeight() {
        return rtmpCamera2.getStreamHeight();
    }

    @Override
    public void enableAudio() {
        rtmpCamera2.enableAudio();
    }

    @Override
    public void disableAudio() {
        rtmpCamera2.disableAudio();
    }

    @Override
    public boolean isAudioMuted() {
        return rtmpCamera2.isAudioMuted();
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
        return rtmpCamera2.prepareAudio(
                attrs.getBitRate(),
                attrs.getSampleRate(),
                attrs.isStereo(),
                attrs.isEchoCanceler(),
                attrs.isNoiseSuppressor()
        );
    }

    @Override
    public boolean isVideoEnabled() {
        return rtmpCamera2.isVideoEnabled();
    }

    private boolean prepareVideo(@NonNull VideoAttributes attrs, int rotation) {
        return rtmpCamera2.prepareVideo(
                attrs.getSize().getWidth(),
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
    public void takePhoto(@NonNull UZTakePhotoCallback callback) {
        rtmpCamera2.getGlInterface().takePhoto(callback::onTakePhoto);
    }

    @Override
    public void startBroadCast(@NonNull String broadCastUrl) {
        rtmpCamera2.startStream(broadCastUrl);
    }

    @Override
    public void stopBroadCast() {
        rtmpCamera2.stopStream();
    }

    @Override
    public boolean isBroadCasting() {
        return rtmpCamera2.isStreaming();
    }

    @Override
    public boolean isFrontCamera() {
        return rtmpCamera2.isFrontCamera();
    }

    @Override
    public void switchCamera() throws UZCameraOpenException {
        try {
            rtmpCamera2.switchCamera();
            if (uzCameraChangeListener != null)
                uzCameraChangeListener.onCameraChange(rtmpCamera2.isFrontCamera());
        } catch (CameraOpenException e) {
            throw new UZCameraOpenException(e.getMessage());
        }
    }

    @Override
    public List<VideoSize> getSupportedResolutions() {
        List<Size> sizes;
        if (rtmpCamera2.isFrontCamera()) {
            sizes = rtmpCamera2.getResolutionsFront();
        } else {
            sizes = rtmpCamera2.getResolutionsBack();
        }
        return ListUtils.map(sizes, VideoSize::fromSize);
    }

    @Override
    public void startPreview(@NonNull CameraHelper.Facing cameraFacing) {
        rtmpCamera2.startPreview(cameraFacing);
    }

    @Override
    public void startPreview(@NonNull CameraHelper.Facing cameraFacing, int w, int h) {
        // because portrait
        rtmpCamera2.startPreview(cameraFacing, h, w);
    }

    @Override
    public boolean isOnPreview() {
        return rtmpCamera2.isOnPreview();
    }

    @Override
    public void stopPreview() {
        rtmpCamera2.stopPreview();
    }

    @Override
    public boolean isRecording() {
        return rtmpCamera2.isRecording();
    }


    @Override
    public void startRecord(@NonNull String savePath) throws IOException {
        if (uzRecordListener != null)
            rtmpCamera2.startRecord(savePath, status -> uzRecordListener.onStatusChange(RecordStatus.lookup(status)));
        else
            rtmpCamera2.startRecord(savePath);
    }

    @Override
    public void stopRecord() {
        rtmpCamera2.stopRecord();
    }


    @Override
    public boolean isLanternSupported() {
        return rtmpCamera2.isLanternSupported();
    }

    @Override
    public void enableLantern() throws Exception {
        rtmpCamera2.enableLantern();
    }

    @Override
    public void disableLantern() {
        rtmpCamera2.disableLantern();
    }

    @Override
    public boolean isLanternEnabled() {
        return rtmpCamera2.isLanternEnabled();
    }

    @Override
    public float getMaxZoom() {
        return rtmpCamera2.getMaxZoom();
    }

    @Override
    public float getZoom() {
        return rtmpCamera2.getZoom();
    }

    @Override
    public void setZoom(float level) {
        rtmpCamera2.setZoom(level);
    }

    @Override
    public void setZoom(@NonNull MotionEvent event) {
        rtmpCamera2.setZoom(event);
    }
}
