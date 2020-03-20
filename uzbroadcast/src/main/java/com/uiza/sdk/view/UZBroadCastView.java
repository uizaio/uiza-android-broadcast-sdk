package com.uiza.sdk.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pedro.encoder.input.gl.render.ManagerRender;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.util.BitrateAdapter;
import com.pedro.rtplibrary.view.OpenGlView;
import com.uiza.sdk.R;
import com.uiza.sdk.enums.AspectRatio;
import com.uiza.sdk.enums.FilterRender;
import com.uiza.sdk.events.EventSignal;
import com.uiza.sdk.events.UZEvent;
import com.uiza.sdk.helpers.Camera1Helper;
import com.uiza.sdk.helpers.Camera2Helper;
import com.uiza.sdk.helpers.ICameraHelper;
import com.uiza.sdk.interfaces.UZBroadCastListener;
import com.uiza.sdk.interfaces.UZCameraChangeListener;
import com.uiza.sdk.interfaces.UZRecordListener;
import com.uiza.sdk.profile.AudioAttributes;
import com.uiza.sdk.profile.VideoAttributes;
import com.uiza.sdk.services.UZRTMPService;
import com.uiza.sdk.util.ValidValues;
import com.uiza.sdk.util.ViewUtil;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

/**
 * @required: <uses-permission android:name="android.permission.CAMERA"/> and
 * <uses-permission android:name="android.permission.RECORD_AUDIO"/>
 */
public class UZBroadCastView extends RelativeLayout {

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    OpenGlView openGlView;
    AspectRatio aspectRatio = AspectRatio.RATIO_16_9;
    private String mainBroadCastUrl;
    private ICameraHelper cameraHelper;

    private ProgressBar progressBar;
    private TextView tvLiveStatus;
    private boolean useCamera2;
    private boolean runInBackground = false;
    private CameraHelper.Facing startCamera = CameraHelper.Facing.FRONT;
    private UZBroadCastListener uzBroadCastListener;
    private long backgroundAllowedDuration = 2 * MINUTE; // default is 2 minutes
    private CountDownTimer backgroundTimer;
    private boolean isBroadcastingBeforeGoingBackground;
    private boolean isFromBackgroundTooLong;
    private boolean AAEnabled = false;
    private boolean keepAspectRatio = false;
    private boolean isFlipHorizontal = false, isFlipVertical = false;
    private boolean adaptiveBitrate = true;
    private BitrateAdapter bitrateAdapter;
    private VideoAttributes videoAttributes;
    private AudioAttributes audioAttributes;

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (uzBroadCastListener != null)
                uzBroadCastListener.surfaceCreated();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            int mHeight = Math.min((int) (width * aspectRatio.getAspectRatio()), height);
            Timber.e("w=%d, h=%d", width, mHeight);
            cameraHelper.startPreview(startCamera, width, mHeight);
            if (runInBackground) {
                cameraHelper.replaceView(openGlView);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && useCamera2)
                postDelayed(UZBroadCastView.this::switchCamera, 100); // fix Note10 with camera2
            if (uzBroadCastListener != null)
                uzBroadCastListener.surfaceChanged(format, width, mHeight);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (runInBackground) {
                if (cameraHelper.isBroadCasting())
                    cameraHelper.replaceView(getContext().getApplicationContext());
                if (cameraHelper.isOnPreview())
                    cameraHelper.stopPreview();
            } else {
                if (cameraHelper.isRecording())
                    cameraHelper.stopRecord();
                if (cameraHelper.isBroadCasting())
                    cameraHelper.stopBroadCast();
                if (cameraHelper.isOnPreview())
                    cameraHelper.stopPreview();
                startBackgroundTimer();
            }
            if (uzBroadCastListener != null)
                uzBroadCastListener.surfaceDestroyed();

        }
    };
    private ConnectCheckerRtmp connectCheckerRtmp = new ConnectCheckerRtmp() {
        @Override
        public void onConnectionSuccessRtmp() {
            if (adaptiveBitrate) {
                bitrateAdapter = new BitrateAdapter(bitrate -> cameraHelper.setVideoBitrateOnFly(bitrate));
                bitrateAdapter.setMaxBitrate(cameraHelper.getBitrate());
            }
            ((Activity) getContext()).runOnUiThread(() -> {
                showLiveStatus();
                progressBar.setVisibility(View.GONE);
                invalidate();
                requestLayout();
                if (uzBroadCastListener != null)
                    uzBroadCastListener.onConnectionSuccess();
            });
            if (runInBackground)
                EventBus.getDefault().postSticky(new UZEvent("Stream started"));
            else
                isBroadcastingBeforeGoingBackground = true;
        }

        @Override
        public void onConnectionFailedRtmp(@NonNull String reason) {
            if (cameraHelper.reTry(5000, reason)) {
                if (runInBackground)
                    EventBus.getDefault().postSticky(new UZEvent("Retry connecting..."));
                if (uzBroadCastListener != null) {
                    ((Activity) getContext()).runOnUiThread(() -> {
                        uzBroadCastListener.onRetryConnection(5000);
                    });
                }
            } else {
                cameraHelper.stopBroadCast();
                ((Activity) getContext()).runOnUiThread(() -> {
                    hideLiveStatus();
                    invalidate();
                    requestLayout();
                    progressBar.setVisibility(View.GONE);
                    if (uzBroadCastListener != null)
                        uzBroadCastListener.onConnectionFailed(reason);
                });
            }
        }

        @Override
        public void onNewBitrateRtmp(long bitrate) {
            if (bitrateAdapter != null && adaptiveBitrate) bitrateAdapter.adaptBitrate(bitrate);
        }

        @Override
        public void onDisconnectRtmp() {
            ((Activity) getContext()).runOnUiThread(() -> {
                hideLiveStatus();
                progressBar.setVisibility(View.GONE);
                invalidate();
                requestLayout();
                if (uzBroadCastListener != null)
                    uzBroadCastListener.onDisconnect();
            });
            // with runInBackground does not post event because service has stopped
            if (runInBackground)
                EventBus.getDefault().postSticky(new UZEvent(EventSignal.STOP, ""));
        }

        @Override
        public void onAuthErrorRtmp() {
            ((Activity) getContext()).runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                invalidate();
                requestLayout();
                if (uzBroadCastListener != null)
                    uzBroadCastListener.onAuthError();
            });
            if (runInBackground)
                EventBus.getDefault().postSticky(new UZEvent("Stream auth error"));
        }

        @Override
        public void onAuthSuccessRtmp() {
            ((Activity) getContext()).runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                invalidate();
                requestLayout();
                if (uzBroadCastListener != null)
                    uzBroadCastListener.onAuthSuccess();
            });
            if (runInBackground)
                EventBus.getDefault().postSticky(new UZEvent("Stream auth success"));
        }
    };

    public UZBroadCastView(Context context) {
        this(context, null);
    }

    public UZBroadCastView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UZBroadCastView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        initView(attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UZBroadCastView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr);
    }

    /**
     * Call twice time
     * Node: Don't call inflate in this method
     */
    private void initView(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.UZBroadCastView, defStyleAttr, 0);
            try {
                boolean hasLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
                useCamera2 = a.getBoolean(R.styleable.UZBroadCastView_useCamera2, hasLollipop);
                runInBackground = a.getBoolean(R.styleable.UZBroadCastView_runInBackground, false);
                if (!useCamera2 && runInBackground) {
                    throw new IllegalArgumentException("the supportRunBackground support camera2 only");
                }
                startCamera = CameraHelper.Facing.values()[a.getInt(R.styleable.UZBroadCastView_startCamera, 1)];
                // for openGL
                keepAspectRatio = a.getBoolean(R.styleable.UZBroadCastView_keepAspectRatio, true);
                AAEnabled = a.getBoolean(R.styleable.UZBroadCastView_AAEnabled, false);
                ManagerRender.numFilters = a.getInt(R.styleable.UZBroadCastView_numFilters, 1);
                isFlipHorizontal = a.getBoolean(R.styleable.UZBroadCastView_isFlipHorizontal, false);
                isFlipVertical = a.getBoolean(R.styleable.UZBroadCastView_isFlipVertical, false);
            } finally {
                a.recycle();
            }
        } else {
            useCamera2 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
            runInBackground = false;
            startCamera = CameraHelper.Facing.FRONT;
            // for OpenGL
            keepAspectRatio = true;
            AAEnabled = false;
            ManagerRender.numFilters = 1;
            isFlipHorizontal = false;
            isFlipVertical = false;
        }
    }

    /**
     * Call one time
     * Note: you must call inflate in this method
     */
    private void onCreateView() {
        inflate(getContext(), R.layout.layout_uiza_glview, this);
        openGlView = findViewById(R.id.camera_view);
        if (useCamera2)
            cameraHelper = new Camera2Helper(openGlView, connectCheckerRtmp);
        else
            cameraHelper = new Camera1Helper(openGlView, connectCheckerRtmp);

        if (runInBackground) {
            UZRTMPService.init(cameraHelper);
        }
        openGlView.init();
        openGlView.getHolder().addCallback(surfaceCallback);
        openGlView.setCameraFlip(isFlipHorizontal, isFlipVertical);
        openGlView.setKeepAspectRatio(keepAspectRatio);
        openGlView.enableAA(AAEnabled);
        tvLiveStatus = findViewById(R.id.live_status);
        progressBar = findViewById(R.id.pb);
        progressBar.getIndeterminateDrawable().setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
        cameraHelper.setConnectReTries(8);
    }

    /**
     * Set AspectRatio
     *
     * @param aspectRatio One of {@link AspectRatio#RATIO_19_9},
     *                    {@link AspectRatio#RATIO_18_9},
     *                    {@link AspectRatio#RATIO_16_9} or {@link AspectRatio#RATIO_4_3}
     */
    public void setAspectRatio(AspectRatio aspectRatio) {
        this.aspectRatio = aspectRatio;
        if (openGlView != null) {
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            openGlView.getLayoutParams().width = screenWidth;
            openGlView.getLayoutParams().height = (int) (screenWidth * aspectRatio.getAspectRatio());
            openGlView.requestLayout();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkLivePermission();
    }

    private void checkLivePermission() {
        Dexter.withActivity((Activity) getContext()).withPermissions(Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    onCreateView();
                    if (uzBroadCastListener != null)
                        uzBroadCastListener.onInit(true);
                } else if (report.isAnyPermissionPermanentlyDenied())
                    showSettingsDialog();
                else
                    showShouldAcceptPermission();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread()
                .check();
    }

    private void showShouldAcceptPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.need_permission);
        builder.setMessage(R.string.this_app_needs_permission);
        builder.setPositiveButton(R.string.okay, (dialog, which) -> checkLivePermission());
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            if (uzBroadCastListener != null)
                uzBroadCastListener.onInit(false);

        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.need_permission);
        builder.setMessage(R.string.this_app_needs_permission_grant_it);
        builder.setPositiveButton(R.string.goto_settings, (dialog, which) -> {
            Intent intent =
                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
            intent.setData(uri);
            ((Activity) getContext()).startActivityForResult(intent, 101);

        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            if (uzBroadCastListener != null)
                uzBroadCastListener.onInit(false);
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * @param adaptiveBitrate boolean
     *                        Default true
     */
    public void setAdaptiveBitrate(boolean adaptiveBitrate) {
        this.adaptiveBitrate = adaptiveBitrate;
    }

    /**
     * @param uzBroadCastListener {@link UZBroadCastListener}
     */
    public void setUZBroadcastListener(UZBroadCastListener uzBroadCastListener) {
        this.uzBroadCastListener = uzBroadCastListener;
    }

    public void setLandscape(boolean landscape) {
        if (cameraHelper != null) cameraHelper.setLandscape(landscape);
    }

    /**
     * Must be called when the app go to resume state
     */
    public void onResume() {
        if (runInBackground && useCamera2) {
            // nothing
        } else {
            checkAndResumeLiveStreamIfNeeded();
            if (isFromBackgroundTooLong) {
                if (uzBroadCastListener != null)
                    uzBroadCastListener.onBackgroundTooLong();
                isFromBackgroundTooLong = false;
            }
        }
    }

    /**
     * Set duration which allows broadcasting to keep the info
     *
     * @param duration the duration which allows broadcasting to keep the info
     */
    public void setBackgroundAllowedDuration(long duration) {
        this.backgroundAllowedDuration = duration;
    }


    private void checkAndResumeLiveStreamIfNeeded() {
        cancelBackgroundTimer();
        if (!isBroadcastingBeforeGoingBackground) return;
        isBroadcastingBeforeGoingBackground = false;
        // We delay a second because the surface need to be resumed before we can prepare something
        // Improve this method whenever you can
        (new Handler()).postDelayed(() -> {
            try {
                stopBroadCast(); // make sure stop stream and start it again
                if (prepareBroadCast() && !TextUtils.isEmpty(mainBroadCastUrl))
                    startBroadCast(mainBroadCastUrl);
            } catch (Exception ignored) {
                Timber.e("Can not resume broadcasting right now !");
            }
        }, SECOND);
    }

    private void startBackgroundTimer() {
        if (backgroundTimer == null) {
            backgroundTimer = new CountDownTimer(backgroundAllowedDuration, SECOND) {
                public void onTick(long millisUntilFinished) {
                    // Nothing
                }

                public void onFinish() {
                    isBroadcastingBeforeGoingBackground = false;
                    isFromBackgroundTooLong = true;
                }
            };
        }
        backgroundTimer.start();
    }

    private void cancelBackgroundTimer() {
        if (backgroundTimer != null) {
            backgroundTimer.cancel();
            backgroundTimer = null;
        }
    }

    /**
     * you must call in onInit()
     *
     * @param uzCameraChangeListener : {@link UZCameraChangeListener} camera witch listener
     */
    public void setUZCameraChangeListener(UZCameraChangeListener uzCameraChangeListener) {
        cameraHelper.setUZCameraChangeListener(uzCameraChangeListener);
    }

    /**
     * you must call in oInit()
     *
     * @param recordListener : record status listener {@link UZRecordListener}
     */
    public void setUZRecordListener(UZRecordListener recordListener) {
        cameraHelper.setUZRecordListener(recordListener);
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
        ViewUtil.blinking(tvLiveStatus);
    }

    /**
     * Each video encoder configuration corresponds to a set of video parameters, including the resolution, frame rate, bitrate, and video orientation.
     * The parameters specified in this method are the maximum values under ideal network conditions.
     * If the video engine cannot render the video using the specified parameters due to poor network conditions,
     * the parameters further down the list are considered until a successful configuration is found.
     * <p>
     * If you do not set the video encoder configuration after joining the channel,
     * you can call this method before calling the enableVideo method to reduce the render time of the first video frame.
     *
     * @param attributes The local video encoder configuration
     */
    public void setVideoAttributes(VideoAttributes attributes) {
        this.videoAttributes = attributes;
        if (cameraHelper != null) cameraHelper.setVideoAttributes(attributes);
    }

    public void setAudioAttributes(AudioAttributes audioAttributes) {
        this.audioAttributes = audioAttributes;
        if (cameraHelper != null) cameraHelper.setAudioAttributes(audioAttributes);
    }

    /**
     * Please call {@link #prepareBroadCast()} before use
     *
     * @param broadCastUrl: Stream Url
     */
    public void startBroadCast(String broadCastUrl) {
        mainBroadCastUrl = broadCastUrl;
        progressBar.setVisibility(View.VISIBLE);
        if (runInBackground && useCamera2) {
            Intent intent = new Intent(getContext().getApplicationContext(), UZRTMPService.class);
            intent.putExtra(UZRTMPService.EXTRA_BROAD_CAST_URL, broadCastUrl);
            getContext().startService(intent);
        } else {
            cameraHelper.startBroadCast(broadCastUrl);
        }
    }

    public boolean isBroadCasting() {
        if (runInBackground)
            return ValidValues.isMyServiceRunning(getContext(), UZRTMPService.class);
        else
            return cameraHelper != null && cameraHelper.isBroadCasting();
    }

    public void stopBroadCast() {
        cameraHelper.stopBroadCast();
    }

    public void switchCamera() {
        cameraHelper.switchCamera();
    }

    /**
     * @param savePath path of save file
     * @throws IOException
     * @required: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     */
    public void startRecord(String savePath) throws IOException {
        cameraHelper.startRecord(savePath);
    }

    /**
     * Check recording
     *
     * @return true if recording
     */
    public boolean isRecording() {
        return cameraHelper.isRecording();
    }

    public void stopRecord() {
        cameraHelper.stopRecord();
    }

    /**
     * Call this method before use {@link #startBroadCast(String)}.
     * Auto detect rotation to prepare for BroadCast
     *
     * @return true if success, false if you get a error (Normally because the encoder selected
     * * doesn't support any configuration seated or your device hasn't a AAC encoder).
     */
    public boolean prepareBroadCast() {
        if (cameraHelper != null) {
            cameraHelper.setAudioAttributes(audioAttributes);
            cameraHelper.setVideoAttributes(videoAttributes);
            return cameraHelper.prepareBroadCast();
        }
        return false;
    }

    /**
     * Call this method before use {@link #startBroadCast(String)}.
     *
     * @param isLandscape:
     * @return true if success, false if you get a error (Normally because the encoder selected
     * * doesn't support any configuration seated or your device hasn't a AAC encoder).
     */
    public boolean prepareBroadCast(boolean isLandscape) {
        if (cameraHelper != null) {
            cameraHelper.setAudioAttributes(audioAttributes);
            cameraHelper.setVideoAttributes(videoAttributes);
            return cameraHelper.prepareBroadCast(isLandscape);
        }
        return false;
    }

    /**
     * @param audioAttributes {@link AudioAttributes} null with out audio
     * @param videoAttributes {@link VideoAttributes}
     * @param isLandscape:    true if broadcast landing
     * @return true if success, false if you get a error (Normally because the encoder selected
     * doesn't support any configuration seated or your device hasn't a AAC encoder).
     */
    public boolean prepareBroadCast(AudioAttributes audioAttributes, @NonNull VideoAttributes videoAttributes, boolean isLandscape) {
        this.videoAttributes = videoAttributes;
        this.audioAttributes = audioAttributes;
        return cameraHelper.prepareBroadCast(audioAttributes, videoAttributes, isLandscape);
    }

    public void enableAA(boolean enable) {
        cameraHelper.enableAA(enable);
    }

    public boolean isRunInBackground() {
        return runInBackground;
    }

    public boolean isAAEnabled() {
        return cameraHelper.isAAEnabled();
    }

    public void setFilter(FilterRender filterRender) {
        cameraHelper.setFilter(filterRender.getFilterRender());
    }

    public void setFilter(int position, FilterRender filterRender) {
        cameraHelper.setFilter(position, filterRender.getFilterRender());
    }

    public int getStreamWidth() {
        return cameraHelper.getStreamWidth();
    }

    // SETTER

    public int getStreamHeight() {
        return cameraHelper.getStreamHeight();
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
