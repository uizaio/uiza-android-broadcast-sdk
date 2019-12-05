package io.uiza.live;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.util.RecordController;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import io.uiza.live.interfaces.CameraChangeListener;
import io.uiza.live.interfaces.ICameraHelper;
import io.uiza.live.interfaces.ProfileEncode;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Helper implements ICameraHelper {

    private RtmpCamera2 rtmpCamera2;

    private CameraChangeListener cameraChangeListener;

    Camera2Helper(@NonNull RtmpCamera2 camera) {
        this.rtmpCamera2 = camera;
    }

    @Override
    public void reTry(long delay) {
        rtmpCamera2.reTry(delay);
    }

    @Override
    public boolean shouldRetry(@NotNull String reason) {
        return rtmpCamera2.shouldRetry(reason);
    }

    @Override
    public void setFilter(@NotNull BaseFilterRender filterReader) {
        rtmpCamera2.getGlInterface().setFilter(filterReader);
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

    public boolean prepareAudio() {
        return rtmpCamera2.prepareAudio();
    }

    @Override
    public boolean prepareAudio(int bitrate, int sampleRate, boolean isStereo) {
        return rtmpCamera2.prepareAudio(bitrate, sampleRate, isStereo, AcousticEchoCanceler.isAvailable(), NoiseSuppressor.isAvailable());
    }

    @Override
    public boolean prepareVideo(@NotNull ProfileEncode profile) {
        return rtmpCamera2.prepareVideo(profile.getWidth(), profile.getHeight(), 24, profile.getBitrate(), false, 90);
    }

    @Override
    public boolean prepareVideo(@NotNull ProfileEncode profile, int fps, int iFrameInterval, int rotation) {
        return rtmpCamera2.prepareVideo(profile.getWidth(), profile.getHeight(), fps, profile.getBitrate(), false, iFrameInterval, rotation);
    }

    @Override
    public void startStream(@NotNull String liveEndpoint) {
        rtmpCamera2.startStream(liveEndpoint);
    }

    @Override
    public void stopStream() {
        rtmpCamera2.stopStream();
    }

    @Override
    public boolean isStreaming() {
        return rtmpCamera2.isStreaming();
    }

    @Override
    public boolean isFrontCamera() {
        return rtmpCamera2.isFrontCamera();
    }

    @Override
    public void switchCamera() throws CameraOpenException {
        rtmpCamera2.switchCamera();
        if (cameraChangeListener != null)
            cameraChangeListener.onCameraChange(rtmpCamera2.isFrontCamera());
    }

    @Override
    public void startPreview(@NotNull CameraHelper.Facing cameraFacing) {
        rtmpCamera2.startPreview(cameraFacing);
    }

    @Override
    public void startPreview(@NotNull CameraHelper.Facing cameraFacing, int width, int height) {
        rtmpCamera2.startPreview(cameraFacing, width, height);
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
    public void startRecord(@NotNull String savePath, @Nullable RecordController.Listener listener) throws IOException {
        rtmpCamera2.startRecord(savePath, listener);
    }

    @Override
    public void stopRecord() {
        rtmpCamera2.stopRecord();
    }

    @Override
    public void setCameraChangeListener(@NonNull CameraChangeListener cameraChangeListener) {
        this.cameraChangeListener = cameraChangeListener;
    }
}
