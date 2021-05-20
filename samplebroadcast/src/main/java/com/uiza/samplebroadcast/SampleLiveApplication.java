package com.uiza.samplebroadcast;

import androidx.multidex.MultiDexApplication;

import com.uiza.sdkbroadcast.UZBroadCast;

import timber.log.Timber;

public class SampleLiveApplication extends MultiDexApplication {

    public static final String EXTRA_STREAM_ENDPOINT = "uiza_live_extra_stream_endpoint";

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        UZBroadCast.init(R.mipmap.ic_launcher);
    }
}
