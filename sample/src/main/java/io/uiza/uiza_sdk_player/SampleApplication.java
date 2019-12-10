package io.uiza.uiza_sdk_player;

import androidx.multidex.MultiDexApplication;

import com.google.android.exoplayer2.offline.DownloadManager;

import io.uiza.core.UizaClient;
import io.uiza.core.api.UizaV3Service;
import io.uiza.core.api.UizaV5Service;
import io.uiza.player.UizaPlayer;
import timber.log.Timber;

public class SampleApplication extends MultiDexApplication {

    public static final String EXTRA_STREAM_ENDPOINT = "uiza_live_extra_stream_endpoint";


    private static final String DEV_HOST = "https://development-api.uizadev.io";
    private static final String DEV_HOST_V3 = "https://teamplayer.uiza.co";
    private static final String APP_SECRET = "uap-c1ffbff4db954ddcb050c6af0b43ba56-41193b64";
    public static final String DF_TOKEN_V3 = "uap-01e137ad1b534004ad822035bf89b29f-b9b31f29";
    public static final String DF_APP_ID = "01e137ad1b534004ad822035bf89b29f";
    private static final String TAG = "SampleApplication";

    public static final String LIVE_URL = "rtmp://35.197.159.184/transcode";
    public static final String STREAM_KEY = "live_XlK2fJWOio";

    public static final String REGION = "asia-southeast-1";
    public static final String APP_ID = "duyqt-app";
    public static final String USER_ID = "duyqt1";

    UizaClient restClient;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        UizaPlayer.get().init(getApplicationContext(), "UizaSampleV5");
//        UizaPlayer.get().setServiceClazz(DemoDownloadService.class);
        restClient = new UizaClient.Builder(getApplicationContext(), DEV_HOST_V3).withToken(DF_TOKEN_V3).builder();
    }

    public UizaV5Service getLiveService() {
        return restClient.createLiveV5Service();
    }

    public UizaV3Service getV3Service() {
        return restClient.createLiveV3Service();
    }

    public DownloadManager getDownloadManager() {
        return UizaPlayer.get().getDownloadManager();
    }


    public static String getLiveEndpoint() {
        return LIVE_URL + "/" + STREAM_KEY;
    }
}
