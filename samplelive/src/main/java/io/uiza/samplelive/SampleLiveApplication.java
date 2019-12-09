package io.uiza.samplelive;

import androidx.multidex.MultiDexApplication;

import io.uiza.core.UizaClient;
import io.uiza.core.api.UizaV5Service;
import io.uiza.live.UizaLive;
import timber.log.Timber;

public class SampleLiveApplication extends MultiDexApplication {

    public static final String EXTRA_STREAM_ENDPOINT = "uiza_live_extra_stream_endpoint";


    private static final String DEV_HOST = "https://development-api.uizadev.io";

    private static final String TAG = "SampleApplication";

    public static final String LIVE_URL = "rtmp://35.240.155.117/transcode";
    public static final String STREAM_KEY = "live_OqHu8SLArw";

    private static final String APP_SECRET = "uap-c1ffbff4db954ddcb050c6af0b43ba56-41193b64";
    public static final String REGION = "asia-southeast-1";
    public static final String APP_ID = "duyqt-app";
    public static final String USER_ID = "duyqt1";

    UizaClient restClient;

    @Override
    public void onCreate() {
        super.onCreate();
//        UizaPlayer.get().setServiceClazz(DemoDownloadService.class);
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
        UizaLive.get().init();
        restClient = new UizaClient.Builder(DEV_HOST).withToken(APP_SECRET).builder();
    }

    public UizaV5Service getLiveService() {
        return restClient.createLiveV5Service();
    }

    public static String getLiveEndpoint() {
        return LIVE_URL + "/" + STREAM_KEY;
    }

}
