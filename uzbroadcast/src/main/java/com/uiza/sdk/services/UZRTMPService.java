package com.uiza.sdk.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.uiza.sdk.R;
import com.uiza.sdk.helpers.ICameraHelper;
import com.uiza.sdk.profile.AudioAttributes;
import com.uiza.sdk.profile.VideoAttributes;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UZRTMPService extends Service {
    public static final String EXTRA_BROAD_CAST_URL = "uz_extra_broad_cast_url";
    public static final String EXTRA_VIDEO_ATTRIBUTES = "uz_extra_video_attributes";
    public static final String EXTRA_AUDIO_ATTRIBUTES = "uz_extra_audio_attributes";
    public static final String EXTRA_BROADCAST_LANDSCAPE = "uz_extra_broad_cast_landscape";
    static ICameraHelper cameraHelper;
    static Context contextApp;
    static NotificationManager notificationManager;
    static int notifyId = 654321;
    static String channelId = "UZStreamChannel";


    public static void init(Context context, ICameraHelper cameraHelper) {
        contextApp = context;
        UZRTMPService.cameraHelper = cameraHelper;
    }

    public static void showNotification(String content) {
        if (contextApp != null) {
            Notification notification = new NotificationCompat.Builder(contextApp, channelId)
                    .setSmallIcon(R.drawable.ic_start_live)
                    .setContentTitle("UZBroadCast")
                    .setContentText(content)
                    .build();
            notificationManager.notify(notifyId, notification);
        }
    }

    // END Static
    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        keepAliveTrick();
    }

    private void keepAliveTrick() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setOngoing(true)
                    .setContentTitle("")
                    .setContentText("")
                    .build();
            startForeground(1, notification);
        } else
            startForeground(1, new Notification());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // STATIC
        String mBroadCastUrl = intent.getStringExtra(EXTRA_BROAD_CAST_URL);
        VideoAttributes videoAttributes = intent.getParcelableExtra(EXTRA_VIDEO_ATTRIBUTES);
        AudioAttributes audioAttributes = intent.getParcelableExtra(EXTRA_AUDIO_ATTRIBUTES);
        boolean landScape = intent.getBooleanExtra(EXTRA_BROADCAST_LANDSCAPE, false);
        if (!TextUtils.isEmpty(mBroadCastUrl)) {
            if (!cameraHelper.isBroadCasting() && videoAttributes != null) {
                if (prepareBroadCast(audioAttributes, videoAttributes, landScape)) {
                    cameraHelper.startBroadCast(mBroadCastUrl);
                }
            } else {
                showNotification("You are already broadcasting :)");
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraHelper == null) return;
        if (cameraHelper.isRecording())
            cameraHelper.stopRecord();
        if (cameraHelper.isBroadCasting()) cameraHelper.stopBroadCast();
    }

    private boolean prepareBroadCast(AudioAttributes audioAttributes, @NonNull VideoAttributes videoAttributes, boolean isLandscape) {
        if (audioAttributes == null)
            return cameraHelper.prepareVideo(videoAttributes, isLandscape ? 0 : 90);
        else
            return cameraHelper.prepareAudio(audioAttributes.setEchoCanceler(false).setNoiseSuppressor(false)) // because run background is us echo and noise
                    && cameraHelper.prepareVideo(videoAttributes, isLandscape ? 0 : 90);
    }
}
