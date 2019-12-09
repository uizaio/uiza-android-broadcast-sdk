package io.uiza.uiza_sdk_player.v5;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;
import io.uiza.core.api.UizaV5Service;
import io.uiza.uiza_sdk_player.R;
import io.uiza.uiza_sdk_player.SampleApplication;

public class UizaTestAPIV5Activity extends AppCompatActivity {

    UizaV5Service v5Service;
    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);
        v5Service = ((SampleApplication) getApplication()).getLiveService();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
