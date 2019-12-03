package io.uiza.live;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.pedro.encoder.input.gl.SpriteGestureController;
import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.gl.render.filters.object.BaseObjectFilterRender;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.view.OpenGlView;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.IOException;

import io.uiza.core.utils.UizaLog;
import io.uiza.live.interfaces.ProfileEncode;
import io.uiza.live.interfaces.UizaLiveListener;

public class UizaOpenGLView extends RelativeLayout implements ConnectCheckerRtmp,
        SurfaceHolder.Callback, View.OnTouchListener {

    private RtmpCamera2 rtmpCamera2; // API 21+
    private SpriteGestureController spriteGestureController = new SpriteGestureController();
    private Handler handler = new Handler();
    private ProfileEncode profile;
    private OpenGlView openGlView;
    private ProgressBar progressBar;
    private TextView tvLiveStatus;

    private UizaLiveListener liveListener;

    public UizaOpenGLView(Context context) {
        this(context, null);
    }

    public UizaOpenGLView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UizaOpenGLView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        initView();
    }

    public UizaOpenGLView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_uiza_glview, this);
        openGlView = findViewById(R.id.open_gl_view);
        tvLiveStatus = findViewById(R.id.tv_live_status);
        progressBar = findViewById(R.id.pb);
        progressBar.getIndeterminateDrawable().setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
        rtmpCamera2 = new RtmpCamera2(openGlView, this);
        rtmpCamera2.setReTries(10);
        openGlView.getHolder().addCallback(this);
        openGlView.setOnTouchListener(this);
    }

    public void setLiveListener(UizaLiveListener liveListener) {
        this.liveListener = liveListener;
    }

    public void hideLiveStatus() {
        if (tvLiveStatus != null) {
            tvLiveStatus.setVisibility(View.GONE);
        }
    }

    public void showLiveStatus() {
        if (tvLiveStatus != null) {
            tvLiveStatus.setVisibility(View.VISIBLE);
            LiveKt.blinking(tvLiveStatus);
        }
    }

    public void setProfile(ProfileEncode profile) {
        this.profile = profile;
    }

    public ProfileEncode getProfile() {
        return profile;
    }

    public void startStream(String liveEndpoint) {
        progressBar.setVisibility(View.VISIBLE);
        rtmpCamera2.startStream(liveEndpoint);
    }

    public boolean isStreaming() {
        return rtmpCamera2.isStreaming();
    }

    public void stopStream() {
        rtmpCamera2.stopStream();
    }

    public void switchCamera() {
        rtmpCamera2.switchCamera();
    }

    public void startRecord(String savePath) throws IOException {
        rtmpCamera2.startRecord(savePath);
    }

    public boolean isRecording() {
        return rtmpCamera2.isRecording();
    }

    public void stopRecord() {
        rtmpCamera2.stopRecord();
    }

    public boolean prepareStream() {
        return rtmpCamera2.prepareAudio() && prepareVideo();
    }


    public boolean prepareAudio() {
        return rtmpCamera2.prepareAudio();
    }

    public boolean prepareVideo() {
        if (profile == null)
            return rtmpCamera2.prepareVideo();
        else {
            int rotation = CameraHelper.getCameraOrientation(getContext());
            return rtmpCamera2.prepareVideo(profile.getWidth(), profile.getHeight(), 24, profile.getBandwidth(), false, rotation);
        }
    }

    public void enableAA(boolean enable) {
        rtmpCamera2.getGlInterface().enableAA(enable);
    }

    public boolean isAAEnabled() {
        return rtmpCamera2.getGlInterface().isAAEnabled();
    }

    public void setBaseObjectFilterRender(BaseObjectFilterRender baseObjectFilterRender) {
        spriteGestureController.setBaseObjectFilterRender(baseObjectFilterRender);
    }

    public void setPreventMoveOutside(boolean preventMoveOutside) {
        spriteGestureController.setPreventMoveOutside(preventMoveOutside);
    }

    public void setFilter(BaseFilterRender baseFilterRender) {
        rtmpCamera2.getGlInterface().setFilter(baseFilterRender);
    }

    public int getStreamWidth() {
        return rtmpCamera2.getStreamWidth();
    }

    public int getStreamHeight() {
        return rtmpCamera2.getStreamHeight();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (liveListener != null) {
            liveListener.surfaceCreated();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        rtmpCamera2.startPreview();
        if (liveListener != null) {
            liveListener.surfaceChanged(format, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (rtmpCamera2.isRecording()) {
            rtmpCamera2.stopRecord();
        }
        if (rtmpCamera2.isStreaming()) {
            rtmpCamera2.stopStream();
        }
        rtmpCamera2.stopPreview();
        if (liveListener != null) {
            liveListener.surfaceDestroyed();
        }
    }

    @Override
    public void onConnectionSuccessRtmp() {
        if (tvLiveStatus != null) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UizaLog.e("UizaOpenGLView", "tvLiveStatus visible");
//                    tvLiveStatus.setVisibility(View.VISIBLE);
                    tvLiveStatus.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_live, null));
                    LiveKt.blinking(tvLiveStatus);
                    progressBar.setVisibility(View.GONE);
                    requestLayout();
                }
            });
        }

        if (liveListener != null) {
            liveListener.onConnectionSuccess();
        }
    }

    @Override
    public void onConnectionFailedRtmp(final String reason) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                rtmpCamera2.stopStream();
                if (tvLiveStatus != null) {
                    tvLiveStatus.setVisibility(View.GONE);
                    tvLiveStatus.clearAnimation();
                }
            }
        });
        if (liveListener != null) {
            liveListener.onConnectionFailed(reason);
        }
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {
        if (liveListener != null) {
            liveListener.onNewBitrate(bitrate);
        }
    }

    @Override
    public void onDisconnectRtmp() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (tvLiveStatus != null) {
                    tvLiveStatus.setVisibility(View.GONE);
                    tvLiveStatus.clearAnimation();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
        if (liveListener != null) {
            liveListener.onDisconnect();
        }
    }

    @Override
    public void onAuthErrorRtmp() {
        if (liveListener != null) {
            liveListener.onAuthError();
        }
    }

    @Override
    public void onAuthSuccessRtmp() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
        if (liveListener != null) {
            liveListener.onAuthSuccess();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (spriteGestureController.spriteTouched(v, event)) {
            spriteGestureController.moveSprite(v, event);
            spriteGestureController.scaleSprite(event);
            return true;
        }
        return false;
    }
}