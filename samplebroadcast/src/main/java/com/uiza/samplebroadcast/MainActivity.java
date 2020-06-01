package com.uiza.samplebroadcast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    AppCompatEditText mServerEdt, mStreamKeyEdt;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);
        mServerEdt = findViewById(R.id.edt_server);
        mStreamKeyEdt = findViewById(R.id.edt_stream_key);
        mServerEdt.setText("rtmp://e8c6034c5d-in.streamwiz.io/live");
        mStreamKeyEdt.setText("live_s13wRzksUu");
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UZBroadCastActivity.class);
            intent.putExtra(SampleLiveApplication.EXTRA_STREAM_ENDPOINT, String.format("%s/%s", mServerEdt.getText().toString(), mStreamKeyEdt.getText().toString()));
            startActivity(intent);
        });
        findViewById(R.id.btn_start_display).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UZDisplayActivity.class);
            intent.putExtra(SampleLiveApplication.EXTRA_STREAM_ENDPOINT, String.format("%s/%s", mServerEdt.getText().toString(), mStreamKeyEdt.getText().toString()));
            startActivity(intent);
        });
        ((AppCompatTextView)findViewById(R.id.txt_info)).setText(String.format(Locale.getDefault(), "%s - %s", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE) );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                launchActivity(SettingsActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private <T extends Activity> void launchActivity(Class<T> tClass) {
        startActivity(new Intent(MainActivity.this, tClass));
    }
}
