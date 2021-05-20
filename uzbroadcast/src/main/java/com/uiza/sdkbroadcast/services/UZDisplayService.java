package com.uiza.sdkbroadcast.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.uiza.sdkbroadcast.R;
import com.uiza.sdkbroadcast.UZBroadCast;
import com.uiza.sdkbroadcast.events.EventSignal;
import com.uiza.sdkbroadcast.events.UZEvent;
import com.uiza.sdkbroadcast.view.UZDisplayBroadCast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UZDisplayService extends Service {
    public static final String EXTRA_BROAD_CAST_URL = "uz_extra_broad_cast_url";
    public static final String EXTRA_VIDEO_ATTRIBUTES = "uz_extra_video_attributes";
    public static final String EXTRA_AUDIO_ATTRIBUTES = "uz_extra_audio_attributes";
    public static final String EXTRA_BROADCAST_LANDSCAPE = "uz_extra_broad_cast_landscape";
    static NotificationManager notificationManager;
    private static UZDisplayBroadCast displayBroadCast;
    private static String channelId = "UZDisplayStreamChannel";
    private static int notifyId = 123456;

    public static void init(UZDisplayBroadCast displayBroadCast) {
        UZDisplayService.displayBroadCast = displayBroadCast;
    }

    private void showNotification(String content) {
        Notification notification = new NotificationCompat.Builder(getBaseContext(), channelId)
                .setSmallIcon(UZBroadCast.getIconNotify())
                .setContentTitle(getBaseContext().getString(R.string.app_name))
                .setContentText(content)
                .build();
        notificationManager.notify(notifyId, notification);
    }

    // END STATIC
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(UZDisplayService.channelId, UZDisplayService.channelId, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        keepAliveTrick();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        showNotification("Stream stopped");
    }

    private void keepAliveTrick() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, UZDisplayService.channelId)
                    .setOngoing(true)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);
        } else {
            startForeground(1, new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().register(this);
        String mBroadCastUrl = intent.getStringExtra(EXTRA_BROAD_CAST_URL);
        if (!TextUtils.isEmpty(mBroadCastUrl)) {
            displayBroadCast.getRtmpDisplay().startStream(mBroadCastUrl);
        }
        return START_STICKY;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void handleEvent(UZEvent event) {
        Timber.e("#handleEvent: called for %s", event.getMessage());
        if (event.getSignal() == EventSignal.STOP)
            stopSelf();
        else
            showNotification(event.getMessage());
    }
}
