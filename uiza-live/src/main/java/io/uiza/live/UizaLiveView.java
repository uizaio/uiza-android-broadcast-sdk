package io.uiza.live;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.util.BitrateAdapter;
import com.pedro.rtplibrary.view.AutoFitTextureView;
import com.pedro.rtplibrary.view.LightOpenGlView;
import com.pedro.rtplibrary.view.OpenGlView;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.IOException;

import io.uiza.core.utils.UizaLog;
import io.uiza.live.interfaces.FilterRender;
import io.uiza.live.interfaces.ICameraHelper;
import io.uiza.live.interfaces.ProfileEncode;
import io.uiza.live.interfaces.UizaLiveListener;

public class UizaLiveView extends RelativeLayout {

    private static final int SURFACE = 0;
    private static final int TEXTURE = 1;
    private static final int OPENGL = 2;
    private static final int LIGH_OPENGL = 3;

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

    private int viewType = OPENGL;
    private ProgressBar progressBar;
    private TextView tvLiveStatus;

    private boolean useCamera2;
    private boolean adaptiveBitrate;

    private UizaLiveListener liveListener;

    private BitrateAdapter bitrateAdapter;

    public UizaLiveView(Context context) {
        this(context, null);
    }

    public UizaLiveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UizaLiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        initView(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UizaLiveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr);
    }

    /**
     * Call twice time
     * Node: Don't call inflate in this method
     */
    private void initView(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.UizaLiveView, defStyleAttr, 0);
        boolean hasLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        viewType = a.getInt(R.styleable.UizaLiveView_viewType, 2);
        useCamera2 = a.getBoolean(R.styleable.UizaLiveView_useCamera2, hasLollipop) && hasLollipop;
        int res = a.getInt(R.styleable.UizaLiveView_videoSize, 360);
        if (res == 1080) {
            profile = ProfileEncode.P1080;
        } else if (res == 720) {
            profile = ProfileEncode.P720;
        } else {
            profile = ProfileEncode.P360;
        }
        fps = a.getInt(R.styleable.UizaLiveView_fps, 24);
        keyframe = a.getInt(R.styleable.UizaLiveView_keyframe, 2);
        adaptiveBitrate = a.getBoolean(R.styleable.UizaLiveView_adaptiveBitrate, false);
        audioStereo = a.getBoolean(R.styleable.UizaLiveView_audioStereo, true);
        audioBitrate = a.getInt(R.styleable.UizaLiveView_audioBitrate, 64) * 1024; //64 Kbps
        audioSampleRate = a.getInt(R.styleable.UizaLiveView_audioSampleRate, 32000); // 32 KHz
    }

    /**
     * Call one time
     * Note: you must call inflate in this method
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        UizaLog.e("UizaLiveView", "viewType: " + viewType);
        switch (viewType) {
            case SURFACE:
                inflate(getContext(), R.layout.layout_uiza_surfaceview, this);
                SurfaceView surfaceView = findViewById(R.id.camera_view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && useCamera2)
                    cameraHelper = new Camera2Helper(new RtmpCamera2(surfaceView, connectCheckerRtmp));
                else
                    cameraHelper = new Camera1Helper(new RtmpCamera1(surfaceView, connectCheckerRtmp));
                surfaceView.getHolder().addCallback(surfaceCallback);
                break;
            case TEXTURE:
                inflate(getContext(), R.layout.layout_uiza_textureview, this);
                AutoFitTextureView textureView = findViewById(R.id.camera_view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && useCamera2) {
                    cameraHelper = new Camera2Helper(new RtmpCamera2(textureView, connectCheckerRtmp));
                } else
                    cameraHelper = new Camera1Helper(new RtmpCamera1(textureView, connectCheckerRtmp));
                textureView.setSurfaceTextureListener(surfaceTextureListener);
                break;
            case LIGH_OPENGL:
                inflate(getContext(), R.layout.layout_uiza_light_glview, this);
                LightOpenGlView lightOpenGlView = findViewById(R.id.camera_view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && useCamera2)
                    cameraHelper = new Camera2Helper(new RtmpCamera2(lightOpenGlView, connectCheckerRtmp));
                else
                    cameraHelper = new Camera1Helper(new RtmpCamera1(lightOpenGlView, connectCheckerRtmp));
                lightOpenGlView.getHolder().addCallback(surfaceCallback);
                break;
            default:
                inflate(getContext(), R.layout.layout_uiza_glview, this);
                OpenGlView openGlView = findViewById(R.id.camera_view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && useCamera2)
                    cameraHelper = new Camera2Helper(new RtmpCamera2(openGlView, connectCheckerRtmp));
                else
                    cameraHelper = new Camera1Helper(new RtmpCamera1(openGlView, connectCheckerRtmp));
                openGlView.getHolder().addCallback(surfaceCallback);
                break;
        }
        tvLiveStatus = findViewById(R.id.live_status);
        progressBar = findViewById(R.id.pb);
        progressBar.getIndeterminateDrawable().setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
        cameraHelper.setConnectReTries(10);
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
        return prepareAudio() && prepareVideo();
    }

    public boolean prepareAudio() {
        return cameraHelper.prepareAudio(audioBitrate, audioSampleRate, audioStereo);
    }

    public boolean prepareVideo() {
        return cameraHelper.prepareVideo(profile, fps, keyframe, CameraHelper.getCameraOrientation(getContext()));
    }

    public void enableAA(boolean enable) {
        if (viewType == 2 || viewType == 3)
            cameraHelper.enableAA(enable);
        else
            UizaLog.e("UizaLiveView", "AA is not support this view");
    }

    public boolean isAAEnabled() {
        if (viewType == 2 || viewType == 3)
            return cameraHelper.isAAEnabled();
        else
            return false;
    }

    public void setFilter(FilterRender filterRender) {
        if (viewType == 2 || viewType == 3)
            cameraHelper.setFilter(filterRender.getFilterRender());
        else
            UizaLog.e("UizaLiveView", "Filter is not support this view");

    }

    public void setFilter(int position, FilterRender filterRender) {
        if (viewType == 2 || viewType == 3)
            cameraHelper.setFilter(position, filterRender.getFilterRender());
        else
            UizaLog.e("UizaLiveView", "Filter is not support this view");
    }

    public int getStreamWidth() {
        return cameraHelper.getStreamWidth();
    }

    public int getStreamHeight() {
        return cameraHelper.getStreamHeight();
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            ((AutoFitTextureView) findViewById(R.id.camera_view)).setAspectRatio(480, 640);
            if (liveListener != null) {
                liveListener.surfaceCreated();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            cameraHelper.startPreview(CameraHelper.Facing.FRONT);
            if (liveListener != null) {
                liveListener.surfaceChanged(0, width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
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
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (liveListener != null) {
                liveListener.surfaceCreated();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && useCamera2) {
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
    };

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

    public boolean supportFilter() {
        return cameraHelper.supportFilter();
    }

    public void setVideoBitrateOnFly(int bitrate) {
        cameraHelper.setVideoBitrateOnFly(bitrate);
    }

    public void enableAudio() {
        cameraHelper.enableAudio();
    }

    public void disableAudio() {
        cameraHelper.disableAudio();
    }

    public boolean isAudioMuted() {
        return cameraHelper.isAudioMuted();
    }

    public boolean isVideoEnabled() {
        return cameraHelper.isVideoEnabled();
    }

    /**
     * Check support Flashlight
     * if use Camera1 always return false
     *
     * @return true if support, false if not support.
     */
    public boolean isLanternSupported() {
        return cameraHelper.isLanternSupported();
    }

    /**
     * @required: <uses-permission android:name="android.permission.FLASHLIGHT"/>
     */
    public void enableLantern() throws Exception {
        cameraHelper.enableLantern();
    }

    /**
     * @required: <uses-permission android:name="android.permission.FLASHLIGHT"/>
     */
    public void disableLantern() {
        cameraHelper.disableLantern();
    }

    public boolean isLanternEnabled() {
        return cameraHelper.isLanternEnabled();
    }
}
