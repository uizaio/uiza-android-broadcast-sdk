package io.uiza.live;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.util.BitrateAdapter;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.IOException;

import io.uiza.live.interfaces.ICameraHelper;
import io.uiza.live.interfaces.ProfileEncode;
import io.uiza.live.interfaces.UizaLiveListener;

public class UizaSurfaceView extends FrameLayout implements
        SurfaceHolder.Callback {

    private ICameraHelper cameraHelper;
    /**
     * ProfileEncoder default 360p
     */
    private ProfileEncode profile;
    /**
     * FPS default 24
     */
    private int fps;

    /**
     * Keyframe default 2
     */
    private int keyframe;
    /**
     * Audio Stereo: default true
     */
    private boolean audioStereo;
    /**
     * Audio Bitrate: default 64 Kbps
     */
    private int audioBitrate;
    /**
     * Audio SampleRate: default 32 KHz
     */
    private int audioSampleRate;

    private ProgressBar progressBar;
    private TextView tvLiveStatus;

    private boolean useCamera2;
    private boolean adaptiveBitrate;

    private UizaLiveListener liveListener;

    private BitrateAdapter bitrateAdapter;

    private SurfaceView surfaceView;

    public UizaSurfaceView(Context context) {
        this(context, null);
    }

    public UizaSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UizaSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        initView(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UizaSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr);
    }

    /**
     * Call twice time
     * Node: Don't call inflate in this method
     *
     * @param attrs
     * @param defStyleAttr
     */
    private void initView(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.UizaOpenGLView, defStyleAttr, 0);
        useCamera2 = a.getBoolean(R.styleable.UizaOpenGLView_useCamera2, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        int res = a.getInt(R.styleable.UizaOpenGLView_videoSize, 360);
        if (res == 1080) {
            profile = ProfileEncode.P1080;
        } else if (res == 720) {
            profile = ProfileEncode.P720;
        } else {
            profile = ProfileEncode.P360;

        }
        fps = a.getInt(R.styleable.UizaOpenGLView_fps, 24);
        keyframe = a.getInt(R.styleable.UizaOpenGLView_keyframe, 2);
        adaptiveBitrate = a.getBoolean(R.styleable.UizaOpenGLView_adaptiveBitrate, false);
        audioStereo = a.getBoolean(R.styleable.UizaOpenGLView_audioStereo, true);
        audioBitrate = a.getInt(R.styleable.UizaOpenGLView_audioBitrate, 64) * 1024; //64 Kbps
        audioSampleRate = a.getInt(R.styleable.UizaOpenGLView_audioSampleRate, 32000); // 32 KHz
    }

    /**
     * Call one time
     * Note: you must call inflate in this method
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inflate(getContext(), R.layout.layout_uiza_surfaceview, this);
        surfaceView = findViewById(R.id.camera_view);
        tvLiveStatus = findViewById(R.id.live_status);
        progressBar = findViewById(R.id.pb);
        progressBar.getIndeterminateDrawable().setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
        if (useCamera2) {
            RtmpCamera2 rtmpCamera2 = new RtmpCamera2(surfaceView, connectCheckerRtmp);
            rtmpCamera2.setReTries(10);
            cameraHelper = new Camera2Helper(rtmpCamera2);
        } else {
            RtmpCamera1 rtmpCamera1 = new RtmpCamera1(surfaceView, connectCheckerRtmp);
            rtmpCamera1.setReTries(10);
            cameraHelper = new Camera1Helper(rtmpCamera1);
        }
        surfaceView.getHolder().addCallback(this);
//        openGlView.setOnTouchListener(this);
    }

    public void setLiveListener(UizaLiveListener liveListener) {
        this.liveListener = liveListener;
    }

    public void hideLiveStatus() {
        if (tvLiveStatus != null) {
            tvLiveStatus.setVisibility(View.GONE);
            tvLiveStatus.clearAnimation();
        }
    }

    /**
     * run on main Thread
     */
    public void showLiveStatus() {
        tvLiveStatus.setVisibility(View.VISIBLE);
        LiveKt.blinking(tvLiveStatus);
    }

    public ProfileEncode getProfile() {
        return profile;
    }

    public void startPreview() {
        cameraHelper.startPreview(cameraHelper.isFrontCamera() ? CameraHelper.Facing.FRONT : CameraHelper.Facing.BACK);
    }

    public void startStream(String liveEndpoint) {
        progressBar.setVisibility(View.VISIBLE);
        cameraHelper.startStream(liveEndpoint);
    }

    public boolean isStreaming() {
        return cameraHelper.isStreaming();
    }

    public void stopStream() {
        cameraHelper.stopStream();
    }

    public void switchCamera() {
        cameraHelper.switchCamera();
    }

    public void startRecord(String savePath) throws IOException {
        cameraHelper.startRecord(savePath, null);
    }

    public boolean isRecording() {
        return cameraHelper.isRecording();
    }

    public void stopRecord() {
        cameraHelper.stopRecord();
    }

    public boolean prepareStream() {
        return cameraHelper.prepareAudio() && prepareVideo();
    }

    public boolean prepareAudio() {
        return cameraHelper.prepareAudio(audioBitrate, audioSampleRate, audioStereo);
    }

    public boolean prepareVideo() {
        return cameraHelper.prepareVideo(profile, fps, keyframe, CameraHelper.getCameraOrientation(getContext()));
    }

    public int getStreamWidth() {
        return cameraHelper.getStreamWidth();
    }

    public int getStreamHeight() {
        return cameraHelper.getStreamHeight();
    }

    private void updateUISurfaceView(int width, int height) {
        if (surfaceView == null) return;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        surfaceView.getLayoutParams().width = screenWidth;
        surfaceView.getLayoutParams().height = width * screenWidth / height;
        surfaceView.requestLayout();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (liveListener != null) {
            liveListener.surfaceCreated();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (useCamera2) {
            cameraHelper.startPreview(CameraHelper.Facing.FRONT, width, height);
        } else {
            cameraHelper.startPreview(CameraHelper.Facing.FRONT);
        }
//            updateUISurfaceView(width, height);
        if (liveListener != null) {
            liveListener.surfaceChanged(format, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (cameraHelper.isRecording()) {
            cameraHelper.stopRecord();
        }
        if (cameraHelper.isStreaming()) {
            cameraHelper.stopStream();
        }
        cameraHelper.stopPreview();
        if (liveListener != null) {
            liveListener.surfaceDestroyed();
        }
    }

    private ConnectCheckerRtmp connectCheckerRtmp = new ConnectCheckerRtmp() {
        @Override
        public void onConnectionSuccessRtmp() {
            if (adaptiveBitrate) {
                bitrateAdapter = new BitrateAdapter(new BitrateAdapter.Listener() {

                    @Override
                    public void onBitrateAdapted(int bitrate) {
                        cameraHelper.setVideoBitrateOnFly(bitrate);
                    }
                });
                bitrateAdapter.setMaxBitrate(cameraHelper.getBitrate());
            }
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLiveStatus();
                    progressBar.setVisibility(View.GONE);
                    invalidate();
                    requestLayout();
                    if (liveListener != null) {
                        liveListener.onConnectionSuccess();
                    }
                }
            });

        }

        @Override
        public void onConnectionFailedRtmp(final String reason) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (cameraHelper.shouldRetry(reason)) {
                        Toast.makeText(getContext(), "Retry", Toast.LENGTH_SHORT)
                                .show();
                        cameraHelper.reTry(5000);  //Wait 5s and retry connect stream
                    } else {
                        cameraHelper.stopStream();
                        progressBar.setVisibility(View.GONE);
                        hideLiveStatus();
                        invalidate();
                        requestLayout();
                        if (liveListener != null) {
                            liveListener.onConnectionFailed(reason);
                        }
                    }
                }
            });

        }

        @Override
        public void onNewBitrateRtmp(long bitrate) {
            if (bitrateAdapter != null) bitrateAdapter.adaptBitrate(bitrate);
            if (liveListener != null) liveListener.onNewBitrate(bitrate);
        }

        @Override
        public void onDisconnectRtmp() {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideLiveStatus();
                    progressBar.setVisibility(View.GONE);
                    invalidate();
                    requestLayout();
                    if (liveListener != null) {
                        liveListener.onDisconnect();
                    }
                }
            });

        }

        @Override
        public void onAuthErrorRtmp() {
            if (liveListener != null) {
                liveListener.onAuthError();
            }
        }

        @Override
        public void onAuthSuccessRtmp() {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    invalidate();
                    requestLayout();
                    if (liveListener != null) {
                        liveListener.onAuthSuccess();
                    }
                }
            });

        }
    };

    // SETTER

    public void setProfile(ProfileEncode profile) {
        this.profile = profile;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setKeyframe(int keyframe) {
        this.keyframe = keyframe;
    }

    public void setAudioStereo(boolean audioStereo) {
        this.audioStereo = audioStereo;
    }

    public void setAudioBitrate(int audioBitrate) {
        this.audioBitrate = audioBitrate * 1024;
    }

    public void setAudioSampleRate(int audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }
}

