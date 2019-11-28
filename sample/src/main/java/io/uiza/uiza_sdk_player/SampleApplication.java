package io.uiza.uiza_sdk_player;

import androidx.multidex.MultiDexApplication;

import com.google.android.exoplayer2.offline.DownloadManager;

import io.uiza.core.UizaClient;
import io.uiza.core.api.UizaLiveV5Service;
import io.uiza.player.UizaPlayer;

public class SampleApplication extends MultiDexApplication {


    private static final String DEV_HOST = "https://development-api.uizadev.io";
    private static final String APP_SECRET = "uap-c1ffbff4db954ddcb050c6af0b43ba56-41193b64";

    private static final String TAG = "SampleApplication";


    UizaClient restClient;

    @Override
    public void onCreate() {
        super.onCreate();
        UizaPlayer.init(getApplicationContext(), "Sample");
//        UizaPlayer.get().setServiceClazz(DemoDownloadService.class);
        restClient = new UizaClient.Builder(DEV_HOST).builder();
        restClient.addHeader("X-Customer-ID", "uiza");
        restClient.addHeader("X-Customer-Custom-ID", "uiza");
        restClient.addHeader("Authorization", APP_SECRET);
        restClient.addHeader("Content-Type", "application/json");
    }

    public UizaLiveV5Service getLiveService() {
        return restClient.createLiveV5Service();
    }

    public DownloadManager getDownloadManager(){
        return UizaPlayer.get().getDownloadManager();
    }
}
