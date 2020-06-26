package com.uiza.samplebroadcast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    AppCompatEditText mServerEdt, mStreamKeyEdt;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);
        mServerEdt = findViewById(R.id.edt_server);
        mStreamKeyEdt = findViewById(R.id.edt_stream_key);
        mServerEdt.setText("rtmp://4e6dfc40ff-in.streamwiz.dev/live");
        mStreamKeyEdt.setText("live_59DB9cIVN9");
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
        if (item.getItemId() == R.id.action_settings) {
            launchActivity(SettingsActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private <T extends Activity> void launchActivity(Class<T> tClass) {
        startActivity(new Intent(MainActivity.this, tClass));
    }
}
