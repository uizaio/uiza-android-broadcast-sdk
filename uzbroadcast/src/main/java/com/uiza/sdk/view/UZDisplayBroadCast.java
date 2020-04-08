package com.uiza.sdk.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.rtmp.RtmpDisplay;
import com.pedro.rtplibrary.util.BitrateAdapter;
import com.uiza.sdk.events.EventSignal;
import com.uiza.sdk.events.UZEvent;
import com.uiza.sdk.interfaces.UZBroadCastListener;
import com.uiza.sdk.profile.AudioAttributes;
import com.uiza.sdk.profile.VideoAttributes;
import com.uiza.sdk.services.UZDisplayService;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

public class UZDisplayBroadCast {
    private final int REQUEST_CODE_STREAM = 2020; //random num
    private final int REQUEST_CODE_RECORD = 2021; //random num
    private RtmpDisplay rtmpDisplay;
    private String mBroadCastUrl;
    private Activity activity;
    private UZBroadCastListener uzBroadCastListener;
    private BitrateAdapter bitrateAdapter;
    private boolean adaptiveBitrate = true;
    private boolean audioPermission = false;
    private VideoAttributes videoAttributes;
    private AudioAttributes audioAttributes;
    private ConnectCheckerRtmp connectCheckerRtmp = new ConnectCheckerRtmp() {
        // IMPLEMENT from ConnectCheckerRtmp
        @Override
        public void onConnectionSuccessRtmp() {
            if (adaptiveBitrate) {
                bitrateAdapter = new BitrateAdapter(bitrate -> rtmpDisplay.setVideoBitrateOnFly(bitrate));
                bitrateAdapter.setMaxBitrate(rtmpDisplay.getBitrate());
            }
            if (uzBroadCastListener != null)
                uzBroadCastListener.onConnectionSuccess();
            EventBus.getDefault().postSticky(new UZEvent("Stream started"));
        }

        @Override
        public void onConnectionFailedRtmp(@NonNull String reason) {
            if (rtmpDisplay.reTry(5000, reason)) {
                EventBus.getDefault().postSticky(new UZEvent("Retry connecting..."));
                if (uzBroadCastListener != null)
                    uzBroadCastListener.onRetryConnection(5000);
            } else {
                rtmpDisplay.stopStream();
                EventBus.getDefault().postSticky(new UZEvent("Connection failed."));
                if (uzBroadCastListener != null)
                    uzBroadCastListener.onConnectionFailed(reason);
            }
        }

        @Override
        public void onNewBitrateRtmp(long bitrate) {
            if (bitrateAdapter != null && adaptiveBitrate) bitrateAdapter.adaptBitrate(bitrate);
        }

        @Override
        public void onDisconnectRtmp() {
            if (uzBroadCastListener != null)
                uzBroadCastListener.onDisconnect();
            EventBus.getDefault().postSticky(new UZEvent(EventSignal.STOP, "Stop"));
        }

        @Override
        public void onAuthErrorRtmp() {
            if (uzBroadCastListener != null)
                uzBroadCastListener.onAuthError();
            EventBus.getDefault().postSticky(new UZEvent(EventSignal.STOP, "Stop"));
        }

        @Override
        public void onAuthSuccessRtmp() {
            if (uzBroadCastListener != null)
                uzBroadCastListener.onAuthSuccess();
            EventBus.getDefault().postSticky(new UZEvent(EventSignal.STOP, ""));
        }
    };

    public UZDisplayBroadCast(Activity activity) {
        this.activity = activity;
        rtmpDisplay = new RtmpDisplay(activity.getApplicationContext(), true, connectCheckerRtmp);
        rtmpDisplay.setReTries(8);
        UZDisplayService.init(this);
        checkLivePermission();
    }

    public RtmpDisplay getRtmpDisplay() {
        return rtmpDisplay;
    }

    private void checkLivePermission() {
        Dexter.withActivity(activity).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                audioPermission = false;
                if (uzBroadCastListener != null)
                    uzBroadCastListener.onInit(false);
            }

            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                audioPermission = true;
                if (uzBroadCastListener != null)
                    uzBroadCastListener.onInit(true);
            }
        }).onSameThread()
                .check();
    }

    /**
     * @param adaptiveBitrate boolean
     *                        Default true
     */
    public void setAdaptiveBitrate(boolean adaptiveBitrate) {
        this.adaptiveBitrate = adaptiveBitrate;
    }

    public AudioAttributes getAudioAttributes() {
        return audioAttributes;
    }

    public void setAudioAttributes(AudioAttributes audioAttributes) {
        this.audioAttributes = audioAttributes;
    }

    public VideoAttributes getVideoAttributes() {
        return videoAttributes;
    }

    public void setVideoAttributes(VideoAttributes videoAttributes) {
        this.videoAttributes = videoAttributes;
    }

    public void setUZBroadCastListener(UZBroadCastListener uzBroadCastListener) {
        this.uzBroadCastListener = uzBroadCastListener;
    }

    public boolean isBroadCasting() {
        return rtmpDisplay != null && rtmpDisplay.isStreaming();
    }

    public void stopBroadCast() {
        rtmpDisplay.stopStream();
    }

    /**
     * Call this method before use {@link #startBroadCast(String)}.
     * Auto detect rotation to prepare for BroadCast
     *
     * @return true if success, false if you get a error (Normally because the encoder selected
     * * doesn't support any configuration seated or your device hasn't a AAC encoder).
     */
    public boolean prepareBroadCast() {
        int rotation = CameraHelper.getCameraOrientation(activity);
        return prepareBroadCast(rotation == 0 || rotation == 180);
    }

    /**
     * Call this method before use {@link #startBroadCast(String)}.
     *
     * @param isLandscape:
     * @return true if success, false if you get a error (Normally because the encoder selected
     * * doesn't support any configuration seated or your device hasn't a AAC encoder).
     */
    public boolean prepareBroadCast(boolean isLandscape) {
        if (videoAttributes == null) {
            Timber.e("Please set videoAttributes");
            return false;
        }
        return prepareBroadCast(audioAttributes, videoAttributes, isLandscape);
    }

    public boolean prepareBroadCast(AudioAttributes audioAttributes, @NonNull VideoAttributes videoAttributes, boolean isLandscape) {
        this.videoAttributes = videoAttributes;
        this.audioAttributes = audioAttributes;
        if (audioAttributes == null)
            return prepareVideo(videoAttributes, isLandscape);
        else
            return prepareAudio(audioAttributes) && prepareVideo(videoAttributes, isLandscape);
    }

    private boolean prepareVideo(@NonNull VideoAttributes attrs, boolean isLandscape) {
        return rtmpDisplay.prepareVideo(
                attrs.getSize().getWidth(),
                attrs.getSize().getHeight(),
                attrs.getFrameRate(),
                attrs.getBitRate(),
                isLandscape ? 0 : 90,
                attrs.getDpi(),
                attrs.getAVCProfile(),
                attrs.getAVCProfileLevel(),
                attrs.getFrameInterval()
        );
    }

    private boolean prepareAudio(@NonNull AudioAttributes attributes) {
        return audioPermission && rtmpDisplay.prepareAudio(
                attributes.getBitRate(),
                attributes.getSampleRate(),
                attributes.isStereo(),
                attributes.isEchoCanceler(),
                attributes.isNoiseSuppressor()
        );
    }

    /**
     * Please call {@link #prepareBroadCast()} before use
     *
     * @param broadCastUrl: Stream Url
     */
    public void startBroadCast(String broadCastUrl) {
        this.mBroadCastUrl = broadCastUrl;
        activity.startActivityForResult(rtmpDisplay.sendIntent(), REQUEST_CODE_STREAM);
    }

    /**
     * call this method in onActivityResult
     *
     * @param requestCode int
     * @param resultCode  int
     * @param data        Intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_CODE_STREAM || requestCode == REQUEST_CODE_RECORD) && resultCode == Activity.RESULT_OK) {
            rtmpDisplay.setIntentResult(resultCode, data);
            Intent intent = new Intent(activity, UZDisplayService.class);
            intent.putExtra(UZDisplayService.EXTRA_BROAD_CAST_URL, mBroadCastUrl);
            activity.startService(intent);
        }
    }
}
