package io.uiza.uiza_sdk_player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import io.uiza.extensions.MediaExtension;
import io.uiza.player.UizaPlayerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.vod_btn).setOnClickListener(this);
        findViewById(R.id.force_vod_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vod_btn:
                startActivity(new Intent(MainActivity.this, VODListActivity.class));
                break;
            case R.id.force_vod_btn:
                startActivity(buildIntent(false,
                        UizaPlayerActivity.ABR_ALGORITHM_RANDOM, "https://tungtt.uiza-dev.workers.dev/00db136b-50b9-4f15-9fd3-1deb1989873e/e83daf9c-2981-4ce7-9a6b-929cc51c0d4d/master.m3u8"));

            default:
                break;
        }
    }

    public Intent buildIntent(boolean preferExtensionDecoders, String abrAlgorithm, String uri) {
        Intent intent = new Intent();
        intent.putExtra(UizaPlayerActivity.PREFER_EXTENSION_DECODERS_EXTRA, preferExtensionDecoders);
        intent.putExtra(UizaPlayerActivity.ABR_ALGORITHM_EXTRA, abrAlgorithm);
        intent.setData(Uri.parse(uri));
        intent.putExtra(UizaPlayerActivity.EXTENSION_EXTRA, MediaExtension.TYPE_HLS);
        intent.setAction(UizaPlayerActivity.ACTION_VIEW);
        return intent;
    }
}
