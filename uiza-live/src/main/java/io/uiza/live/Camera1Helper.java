package io.uiza.live;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;

import androidx.annotation.NonNull;

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.util.RecordController;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import io.uiza.live.interfaces.CameraChangeListener;
import io.uiza.live.interfaces.ICameraHelper;
import io.uiza.live.interfaces.ProfileEncode;
import io.uiza.live.interfaces.UizaCameraOpenException;

public class Camera1Helper implements ICameraHelper {

    private RtmpCamera1 rtmpCamera1;

    private CameraChangeListener cameraChangeListener;

    Camera1Helper(@NonNull RtmpCamera1 camera) {
        this.rtmpCamera1 = camera;
    }

    @Override
    public void reTry(long delay) {
        rtmpCamera1.reTry(delay);
    }

    @Override
    public boolean shouldRetry(@NotNull String reason) {
        return rtmpCamera1.shouldRetry(reason);
    }

    @Override
    public void setCameraChangeListener(@NonNull CameraChangeListener cameraChangeListener) {
        this.cameraChangeListener = cameraChangeListener;
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
    public boolean prepareAudio() {
        return rtmpCamera1.prepareAudio();
    }

    @Override
    public boolean prepareAudio(int bitrate, int sampleRate, boolean isStereo) {
        return rtmpCamera1.prepareAudio(bitrate, sampleRate, isStereo, AcousticEchoCanceler.isAvailable(), NoiseSuppressor.isAvailable());
    }

    @Override
    public boolean prepareVideo(@NotNull ProfileEncode profile) {
        return rtmpCamera1.prepareVideo(profile.getWidth(), profile.getHeight(), 24, profile.getBitrate(), false, 90);
    }

    @Override
    public boolean prepareVideo(@NotNull ProfileEncode profile, int fps, int iFrameInterval, int rotation) {
        return rtmpCamera1.prepareVideo(profile.getWidth(), profile.getHeight(), fps, profile.getBitrate(), false, iFrameInterval, rotation);
    }

    @Override
    public void startStream(@NotNull String liveEndpoint) {
        rtmpCamera1.startStream(liveEndpoint);
    }

    @Override
    public void stopStream() {
        rtmpCamera1.stopStream();
    }

    @Override
    public boolean isStreaming() {
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
    public void switchCamera() throws UizaCameraOpenException {
        try {
            rtmpCamera1.switchCamera();
        } catch (CameraOpenException e) {
            throw new UizaCameraOpenException(e.getMessage());
        }
        if (cameraChangeListener != null)
            cameraChangeListener.onCameraChange(rtmpCamera1.isFrontCamera());
    }

    @Override
    public void startPreview(@NotNull CameraHelper.Facing cameraFacing) {
        rtmpCamera1.startPreview(cameraFacing);
    }

    @Override
    public void startPreview(@NotNull CameraHelper.Facing cameraFacing, int width, int height) {
        rtmpCamera1.startPreview(cameraFacing, width, height);
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
    public void startRecord(@NotNull String savePath, @Nullable RecordController.Listener listener) throws IOException {
        rtmpCamera1.startRecord(savePath, listener);

    }

    @Override
    public void stopRecord() {
        rtmpCamera1.stopRecord();
        rtmpCamera1.startPreview();
    }


}
