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
import com.uiza.sdkbroadcast.helpers.ICameraHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UZRTMPService extends Service {
    public static final String EXTRA_BROAD_CAST_URL = "uz_extra_broad_cast_url";
    static ICameraHelper cameraHelper;

    static NotificationManager notificationManager;
    static int notifyId = 654321;
    static String channelId = "UZStreamChannel";


    public static void init(ICameraHelper cameraHelper) {
        UZRTMPService.cameraHelper = cameraHelper;
    }

    private void showNotification(String content) {
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(UZBroadCast.getIconNotify())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content)
                .build();
        notificationManager.notify(notifyId, notification);
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
            startForeground(101, notification);
        } else
            startForeground(101, new Notification());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // STATIC
        EventBus.getDefault().register(this);
        String mBroadCastUrl = intent.getStringExtra(EXTRA_BROAD_CAST_URL);
        if (!TextUtils.isEmpty(mBroadCastUrl)) {
            cameraHelper.stopBroadCast();
            if (!cameraHelper.isBroadCasting()) {
                if (cameraHelper.prepareBroadCast()) {
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
        EventBus.getDefault().unregister(this);
        showNotification("Stream stopped");
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
