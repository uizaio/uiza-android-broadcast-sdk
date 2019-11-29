package io.uiza.player.views;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.MediaSourceEventListener;

import java.util.Locale;

import io.uiza.core.utils.StringKt;
import io.uiza.player.BuildConfig;
import io.uiza.player.R;
import io.uiza.player.observers.AudioVolumeObserver;
import io.uiza.player.observers.OnAudioVolumeChangedListener;
import io.uiza.player.utils.ConvertKt;
import io.uiza.player.utils.DeviceKt;

public class UizaDebugView extends RelativeLayout implements AnalyticsListener, OnAudioVolumeChangedListener {
    private TextView textEntityId, textBufferHealth, textNetworkActivity, textVolume, textViewPortFrame,
            textConnectionSpeed, textHost, textVersion, textDeviceInfo, textVideoFormat, textAudioFormat,
            textResolution;

    Handler handler = new Handler();
    private AudioVolumeObserver volumeObserver;
    private int droppedFrames;
    private int surfaceWidth, surfaceHeight;
    private int optimalResWidth, optimalResHeight;
    private int currentResWidth, currentResHeight;
    private String audioDecodesStr = StringKt.emptyString, videoDecoderStr = StringKt.emptyString;
    private String audioInfo = StringKt.emptyString, videoInfo = StringKt.emptyString;

    public UizaDebugView(Context context) {
        super(context);
        init();
    }

    public UizaDebugView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UizaDebugView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UizaDebugView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_debug_view, this);
        textEntityId = findViewById(R.id.text_entity_id);
        textBufferHealth = findViewById(R.id.text_buffer_health);
        textNetworkActivity = findViewById(R.id.text_network_activity);
        textVolume = findViewById(R.id.text_volume);
        textViewPortFrame = findViewById(R.id.text_viewport_frame);
        textConnectionSpeed = findViewById(R.id.text_connection_speed);
        textHost = findViewById(R.id.text_host);
        textVersion = findViewById(R.id.text_versions);
        textDeviceInfo = findViewById(R.id.text_device_info);
        textVideoFormat = findViewById(R.id.text_video_format);
        textAudioFormat = findViewById(R.id.text_audio_format);
        textResolution = findViewById(R.id.text_current_optimal_res);

        droppedFrames = 0;
        // close button
        String volumeInfo = String.format(Locale.US, "%d/%d",
                DeviceKt.currentVolume(getContext(), AudioManager.STREAM_MUSIC),
                DeviceKt.maxVolume(getContext(), AudioManager.STREAM_MUSIC));
        textVolume.setText(volumeInfo);
        textDeviceInfo.setText(DeviceKt.deviceInfo());
        textVersion.setText(String.format(Locale.US, "%s / %s / %s", "uiza-sdk-v5", BuildConfig.EXO_VERSION, "V5"));
    }

    public void setCloseClick(OnClickListener closeClick) {
        findViewById(R.id.btn_close).setOnClickListener(closeClick);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (volumeObserver == null) {
            volumeObserver = new AudioVolumeObserver(getContext(), handler);
        }
        volumeObserver.register(AudioManager.STREAM_MUSIC, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (volumeObserver != null) {
            volumeObserver.unregister();
            volumeObserver = null;
        }
    }

    @Override
    public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {
        if (trackType == C.TRACK_TYPE_VIDEO) {
            videoDecoderStr = getDecoderCountersBufferCountString(decoderCounters);
            depictVideoDetailInfo();
        } else if (trackType == C.TRACK_TYPE_AUDIO) {
            audioDecodesStr = getDecoderCountersBufferCountString(decoderCounters);
            depictAudioDetailInfo();
        }
    }


    @Override
    public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {
        if (format == null) return;
        if (trackType == C.TRACK_TYPE_AUDIO) {
            audioInfo = getResources().getString(R.string.stat_audio_format, format.sampleMimeType, format.sampleRate, format.channelCount);
            depictAudioDetailInfo();
        } else if (trackType == C.TRACK_TYPE_VIDEO) {
            videoInfo = getResources().getString(R.string.stat_video_format, format.sampleMimeType, format.width,
                    format.height, Math.round(format.frameRate), getPixelAspectRatioString(format.pixelWidthHeightRatio));
            depictVideoDetailInfo();
        }
    }

    private void depictAudioDetailInfo() {
        setTextAudioFormat(audioInfo + audioDecodesStr);
    }

    private void depictVideoDetailInfo() {
        setTextVideoFormat(videoInfo + videoDecoderStr);
    }

    @Override
    public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {
        this.droppedFrames += droppedFrames;
        setViewPortFrame();

    }

    @Override
    public void onSurfaceSizeChanged(EventTime eventTime, int width, int height) {
        surfaceWidth = width;
        surfaceHeight = height;
        setViewPortFrame();
    }

    @Override
    public void onAudioVolumeChanged(int currentVolume, int maxVolume) {
        handler.post(() -> textVolume.setText(String.format(Locale.US, "%d / %d", currentVolume, maxVolume)));
    }

    @Override
    public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
//        UizaLog.e("UizaDebugView", "totalBufferedDurationMs" + eventTime.totalBufferedDurationMs);
        handler.post(() -> {
            textConnectionSpeed.setText(ConvertKt.bandwidthFormat(bitrateEstimate));
            textNetworkActivity.setText(ConvertKt.humanReadableByteCount(totalBytesLoaded, true, false));
            textBufferHealth.setText(getResources().getString(R.string.stat_buffer_health_format, ConvertKt.formatDouble((eventTime.totalBufferedDurationMs / Math.pow(10, 3)), 1)));
        });
    }


    @Override
    public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (width <= 0 || height <= 0) return;
        if (width != currentResWidth && height != currentResHeight) {
            currentResWidth = width;
            currentResHeight = height;
            handler.post(() -> textResolution.setText(getResources().getString(R.string.stat_resolution_format,
                    currentResWidth, currentResHeight, optimalResWidth, optimalResHeight)));
        }
    }

    @Override
    public void onLoadCompleted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        Format downloadFormat = mediaLoadData.trackFormat;
        if (downloadFormat != null && downloadFormat.width != -1 && downloadFormat.height != -1) {
            if (downloadFormat.width != optimalResWidth && downloadFormat.height != optimalResHeight) {
                optimalResWidth = downloadFormat.width;
                optimalResHeight = downloadFormat.height;
                handler.post(() -> textResolution.setText(getResources().getString(R.string.stat_resolution_format,
                        currentResWidth, currentResHeight, optimalResWidth, optimalResHeight)));
            }
        }
    }

    private static String getPixelAspectRatioString(float pixelAspectRatio) {
        return pixelAspectRatio == Format.NO_VALUE || pixelAspectRatio == 1f ? ""
                : (" par:" + String.format(Locale.US, "%.02f", pixelAspectRatio));
    }

    private static String getDecoderCountersBufferCountString(DecoderCounters counters) {
        if (counters == null) {
            return StringKt.emptyString;
        }
        counters.ensureUpdated();
        return " (sib:" + counters.skippedInputBufferCount +
                " sb:" + counters.skippedOutputBufferCount +
                " rb:" + counters.renderedOutputBufferCount +
                " db:" + counters.droppedBufferCount +
                " mcdb:" + counters.maxConsecutiveDroppedBufferCount +
                " dk:" + counters.droppedToKeyframeCount +
                ")";
    }

    /**
     * Depict Entity id
     *
     * @param value should be formatted like below
     *              EX: c62a5409-0e8a-4b11-8e0d-c58c43d81b60
     */
    public void setEntityInfo(String value) {
        textEntityId.setText(value);
    }

    /**
     * Depict Buffer health
     *
     * @param value should be formatted like below
     *              EX: 20 s
     */
    public void setTextBufferHealth(String value) {
        textBufferHealth.setText(value);
    }

    /**
     * Depict Network Activity
     *
     * @param value should be formatted like below
     *              EX: 5 kB or 5 MB
     */
    public void setTextNetworkActivity(String value) {
        textNetworkActivity.setText(value);
    }

    /**
     * Depict Volume
     *
     * @param value should be formatted like below
     *              EX: 50%
     */
    public void setTextVolume(String value) {
        textVolume.setText(value);
    }

    /**
     * Depict Viewport and dropped frames
     * <p>
     * EX: 806x453 / 0 dropped frames
     */
    private void setViewPortFrame() {
        if (surfaceWidth == 0 && surfaceHeight == 0) {
            // at first time, surface view size or viewport equals to uzVideo size
            surfaceWidth = this.getWidth();
            surfaceHeight = this.getHeight();
        }
        handler.post(() -> textViewPortFrame.setText(
                getResources().getString(R.string.stat_viewport_frame_format, surfaceWidth, surfaceHeight, droppedFrames)));
    }

    /**
     * Depict connection speed
     *
     * @param value should be formatted like below
     *              EX: 40 mbps or 40 kbps
     */
    public void setTextConnectionSpeed(String value) {
        textConnectionSpeed.setText(value);
    }

    /**
     * Depict host
     *
     * @param value should be formatted like below
     *              EX: https://uiza.io
     */
    public void setTextHost(String value) {
        textHost.setText(value);
    }

    /**
     * Depict version
     *
     * @param value should be formatted like below
     *              EX: SDK Version / Player Version / API Version
     */
    public void setTextVersion(String value) {
        textVersion.setText(value);
    }

    /**
     * Depict Device Info
     *
     * @param value should be formatted like below
     *              EX: Device Name / OS Version
     */
    public void setTextDeviceInfo(String value) {
        textDeviceInfo.setText(value);
    }

    /**
     * Depict Video format
     *
     * @param value should be formatted like below
     *              Ex: video/avc 1280x720@30
     */
    public void setTextVideoFormat(String value) {
        textVideoFormat.setText(value);
    }

    /**
     * Depict audio format
     *
     * @param value should be formatted like below
     *              Ex: audio/mp4a-latm 48000Hz
     */
    public void setTextAudioFormat(String value) {
        textAudioFormat.setText(value);
    }

    /**
     * Depict current / optimal (download) resolution
     *
     * @param value should be formatted like below
     *              Ex: 1280x720 /
     */
    public void setTextResolution(String value) {
        textResolution.setText(value);
    }
}