package com.uiza.sdkbroadcast.interfaces;

public interface UZBroadCastListener {

    void onInit(boolean success);

    void onConnectionSuccess();

    void onConnectionFailed(String reason);

    void onRetryConnection(long delay);

    void onDisconnect();

    void onAuthError();

    void onAuthSuccess();

    void surfaceCreated();

    void surfaceChanged(
            int format, int width, int
            height
    );

    void surfaceDestroyed();

    void onBackgroundTooLong();
}
