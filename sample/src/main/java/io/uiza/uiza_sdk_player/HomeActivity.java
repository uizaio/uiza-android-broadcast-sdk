package io.uiza.uiza_sdk_player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import io.uiza.uiza_sdk_player.live.CreateLiveActivity;
import io.uiza.uiza_sdk_player.live.UizaLiveActivity;
import io.uiza.uiza_sdk_player.vod.VODListActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.vod_btn).setOnClickListener(this);
        findViewById(R.id.live_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vod_btn:
                startActivity(new Intent(HomeActivity.this, VODListActivity.class));
                break;
            case R.id.live_btn:
                Intent liveIntent = new Intent(HomeActivity.this, CreateLiveActivity.class);
                startActivity(liveIntent);
                break;
            default:
                break;
        }
    }
}
