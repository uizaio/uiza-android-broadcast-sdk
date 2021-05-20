package com.uiza.samplebroadcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.uiza.sdkbroadcast.enums.Translate;
import com.uiza.sdkbroadcast.interfaces.UZBroadCastListener;
import com.uiza.sdkbroadcast.profile.AudioAttributes;
import com.uiza.sdkbroadcast.profile.VideoAttributes;
import com.uiza.sdkbroadcast.view.UZDisplayBroadCast;
import com.uiza.widget.UZMediaButton;

import timber.log.Timber;

public class UZDisplayActivity extends AppCompatActivity implements View.OnClickListener, UZBroadCastListener, Constant {

    UZMediaButton startBtn;
    SharedPreferences preferences;
    String broadCastUrl;
    UZDisplayBroadCast broadCast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        startBtn = findViewById(R.id.btn_start);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        broadCast = new UZDisplayBroadCast(this);
        broadCast.setUZBroadCastListener(this);
        broadCastUrl = getIntent().getStringExtra(SampleLiveApplication.EXTRA_STREAM_ENDPOINT);
        if (TextUtils.isEmpty(broadCastUrl)) {
            finish();
        }
        int profile = Integer.parseInt(preferences.getString(PREF_CAMERA_PROFILE, DEFAULT_CAMERA_PROFILE));
        int maxBitrate = Integer.parseInt(preferences.getString(PREF_VIDEO_BITRATE, DEFAULT_MAX_BITRATE));
        int fps = Integer.parseInt(preferences.getString(PREF_FPS, DEFAULT_FPS));
        int frameInterval = Integer.parseInt(preferences.getString(PREF_FRAME_INTERVAL, DEFAULT_FRAME_INTERVAL));
        int audioBitrate = Integer.parseInt(preferences.getString(PREF_AUDIO_BITRATE, DEFAULT_AUDIO_BITRATE));
        int audioSampleRate = Integer.parseInt(preferences.getString(PREF_SAMPLE_RATE, DEFAULT_SAMPLE_RATE));
        boolean stereo = preferences.getBoolean(PREF_AUDIO_STEREO, DEFAULT_AUDIO_STEREO);
        VideoAttributes videoAttributes;
        if (profile == 1080)
            videoAttributes = VideoAttributes.FHD_1080p(fps, maxBitrate, frameInterval);
        else if (profile == 480)
            videoAttributes = VideoAttributes.SD_480p(fps, maxBitrate, frameInterval);
        else if (profile == 360)
            videoAttributes = VideoAttributes.SD_360p(fps, maxBitrate, frameInterval);
        else
            videoAttributes = VideoAttributes.HD_720p(fps, maxBitrate, frameInterval);
        AudioAttributes audioAttributes = AudioAttributes.create(audioBitrate, audioSampleRate, stereo);
        // set audio and video profile
        broadCast.setVideoAttributes(videoAttributes);
        broadCast.setAudioAttributes(audioAttributes);
        startBtn.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (broadCast != null) {
            startBtn.setChecked(broadCast.isBroadCasting());
        }
        super.onResume();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stop");
        builder.setMessage("Do you want to stop?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            super.onBackPressed();
            broadCast.stopBroadCast();
            dialog.dismiss();
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (broadCast != null && broadCast.isBroadCasting())
            showExitDialog();
        else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            if (!broadCast.isBroadCasting()) {
                if (broadCast.prepareBroadCast()) {
                    broadCast.startBroadCast(broadCastUrl);
                    startBtn.setChecked(true);
                } else {
                    Toast.makeText(this, "Error preparing stream, This device cant do it",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                broadCast.stopBroadCast();
                startBtn.setChecked(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        broadCast.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onInit(boolean success) {
        Timber.i("audioPermission %b", success);
    }

    @Override
    public void onConnectionSuccess() {
        runOnUiThread(() -> {
                    broadCast.setImageWatermark(R.drawable.logo, new PointF(20f, 15f), Translate.TOP_LEFT);
                    startBtn.setChecked(true);
                    Toast.makeText(this, "onConnectionSuccess",
                            Toast.LENGTH_SHORT).show();
                }
        );
    }

    @Override
    public void onConnectionFailed(String reason) {
        runOnUiThread(() ->
                Toast.makeText(this, "onConnectionFailed",
                        Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onRetryConnection(long delay) {
        runOnUiThread(() -> {
                    startBtn.setChecked(broadCast.isBroadCasting());
                    Toast.makeText(this, "onRetryConnection: " + delay,
                            Toast.LENGTH_SHORT).show();
                }
        );
    }

    @Override
    public void onDisconnect() {
        runOnUiThread(() ->
                Toast.makeText(this, "onDisconnect",
                        Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onAuthError() {
        runOnUiThread(() ->
                Toast.makeText(this, "onAuthError",
                        Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onAuthSuccess() {
        runOnUiThread(() ->
                Toast.makeText(this, "onAuthSuccess",
                        Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void surfaceCreated() {

    }

    @Override
    public void surfaceChanged(int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void onBackgroundTooLong() {

    }
}
